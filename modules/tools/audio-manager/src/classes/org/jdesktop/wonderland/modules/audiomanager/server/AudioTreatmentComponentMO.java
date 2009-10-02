/**
 * Project Wonderland
 *
 * Copyright (c) 2004-2009, Sun Microsystems, Inc., All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * Sun designates this particular file as subject to the "Classpath" 
 * exception as provided by Sun in the License file that accompanied 
 * this code.
 */
package org.jdesktop.wonderland.modules.audiomanager.server;

import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingCapsule;
import com.jme.bounding.BoundingSphere;
import com.jme.bounding.BoundingVolume;
import com.jme.bounding.OrientedBoundingBox;

import com.jme.math.Vector3f;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.DataManager;
import com.sun.sgs.app.ManagedReference;

import com.sun.sgs.kernel.KernelRunnable;

import com.sun.sgs.service.NonDurableTransactionParticipant;
import com.sun.sgs.service.Transaction;

import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Serializable;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import org.jdesktop.wonderland.server.cell.annotation.UsesCellComponentMO;

import org.jdesktop.wonderland.common.cell.messages.CellMessage;

import org.jdesktop.wonderland.common.cell.state.CellComponentServerState;

import org.jdesktop.wonderland.server.cell.AbstractComponentMessageReceiver;
import org.jdesktop.wonderland.server.cell.CellComponentMO;
import org.jdesktop.wonderland.server.cell.CellMO;
import org.jdesktop.wonderland.server.cell.CellParentChangeListenerSrv;
import org.jdesktop.wonderland.server.cell.ChannelComponentMO;
import org.jdesktop.wonderland.server.cell.ProximityComponentMO;

import org.jdesktop.wonderland.modules.audiomanager.common.AudioManagerConnectionType;
import org.jdesktop.wonderland.modules.audiomanager.common.AudioTreatmentComponentClientState;
import org.jdesktop.wonderland.modules.audiomanager.common.AudioTreatmentComponentServerState;
import org.jdesktop.wonderland.modules.audiomanager.common.AudioTreatmentComponentServerState.PlayWhen;
import org.jdesktop.wonderland.modules.audiomanager.common.VolumeUtil;

import org.jdesktop.wonderland.modules.audiomanager.common.messages.AudioTreatmentDoneMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.AudioTreatmentMenuChangeMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.AudioTreatmentRequestMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.AudioVolumeMessage;

import org.jdesktop.wonderland.modules.presencemanager.common.PresenceInfo;

import com.sun.voip.client.connector.CallStatus;
import com.sun.voip.client.connector.CallStatusListener;

import com.sun.mpk20.voicelib.app.Call;
import com.sun.mpk20.voicelib.app.DefaultSpatializer;
import com.sun.mpk20.voicelib.app.FalloffFunction;
import com.sun.mpk20.voicelib.app.FullVolumeSpatializer;
import com.sun.mpk20.voicelib.app.ManagedCallStatusListener;
import com.sun.mpk20.voicelib.app.Player;
import com.sun.mpk20.voicelib.app.Spatializer;
import com.sun.mpk20.voicelib.app.Treatment;
import com.sun.mpk20.voicelib.app.TreatmentGroup;
import com.sun.mpk20.voicelib.app.TreatmentSetup;
import com.sun.mpk20.voicelib.app.VoiceManager;
import com.sun.mpk20.voicelib.app.VoiceManagerParameters;

import org.jdesktop.wonderland.common.cell.CallID;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.cell.ClientCapabilities;
import org.jdesktop.wonderland.common.cell.state.CellComponentClientState;

import org.jdesktop.wonderland.common.checksums.Checksum;
import org.jdesktop.wonderland.common.checksums.ChecksumList;
import org.jdesktop.wonderland.server.WonderlandContext;
import org.jdesktop.wonderland.server.comms.WonderlandClientID;
import org.jdesktop.wonderland.server.comms.WonderlandClientSender;

/**
 *
 * @author jprovino
 */
public class AudioTreatmentComponentMO extends AudioParticipantComponentMO
	implements CellParentChangeListenerSrv, ManagedCallStatusListener {

    private static final Logger logger =
            Logger.getLogger(AudioTreatmentComponentMO.class.getName());

    private static final String ASSET_PREFIX = "wonderland-web-asset/asset/";

    private String groupId;
    private String[] treatments = new String[0];
    private double volume = 1;
    private PlayWhen playWhen = PlayWhen.ALWAYS;
    private boolean playOnce = false;
    private double extent = 10;
    private boolean useCellBounds = false;
    private double fullVolumeAreaPercent = 25;
    private boolean distanceAttenuated = true;
    private double falloff = 50;
    private boolean showBounds = false;

    /** the channel from that cell */
    @UsesCellComponentMO(ChannelComponentMO.class)
    private ManagedReference<ChannelComponentMO> channelRef;

    private CellID cellID;

    private static String serverURL;

    static {
        serverURL = System.getProperty("wonderland.web.server.url");
    }

    /**
     * Create a AudioTreatmentComponent for the given cell. The cell must already
     * have a ChannelComponent otherwise this method will throw an IllegalStateException
     * @param cell
     */
    public AudioTreatmentComponentMO(CellMO cellMO) {
        super(cellMO);

	cellID = cellMO.getCellID();

        // The AudioTreatment Component depends upon the Proximity Component.
        // We add this component as a dependency if it does not yet exist
        if (cellMO.getComponent(ProximityComponentMO.class) == null) {
            cellMO.addComponent(new ProximityComponentMO(cellMO));
        }

        cellMO.addParentChangeListener(this);
	//System.out.println("Added parent change listener");
    }

    @Override
    public void setServerState(CellComponentServerState serverState) {
	super.setServerState(serverState);

	if (isLive()) {
	    cleanup();
	}

        AudioTreatmentComponentServerState state = (AudioTreatmentComponentServerState) serverState;

        groupId = state.getGroupId();

	if (groupId == null || groupId.length() == 0) {
	    groupId = CallID.getCallID(cellID);
	}

        treatments = state.getTreatments();

	volume = state.getVolume();

	playWhen = state.getPlayWhen();

	playOnce = state.getPlayOnce();

	extent = state.getExtent();

	useCellBounds = state.getUseCellBounds();

	fullVolumeAreaPercent = state.getFullVolumeAreaPercent();

 	distanceAttenuated = state.getDistanceAttenuated();

	falloff = state.getFalloff();

	showBounds = state.getShowBounds();

	if (isLive()) {
	    initialize();
	}
    }

    @Override
    public CellComponentServerState getServerState(CellComponentServerState serverState) {
        AudioTreatmentComponentServerState state = (AudioTreatmentComponentServerState) serverState;

        if (state == null) {
            state = new AudioTreatmentComponentServerState();

	    if (groupId == null || groupId.length() == 0) {
	        groupId = CallID.getCallID(cellID);
	    }

            state.setGroupId(groupId);
            state.setTreatments(treatments);
	    state.setVolume(volume);
	    state.setPlayWhen(playWhen);
	    state.setPlayOnce(playOnce);
	    state.setExtent(extent);
	    state.setUseCellBounds(useCellBounds);
	    state.setFullVolumeAreaPercent(fullVolumeAreaPercent);
	    state.setDistanceAttenuated(distanceAttenuated);
	    state.setFalloff(falloff);
	    state.setShowBounds(showBounds);
        }

        return super.getServerState(state);
    }

    @Override
    public CellComponentClientState getClientState(
            CellComponentClientState clientState,
            WonderlandClientID clientID,
            ClientCapabilities capabilities) {

	AudioTreatmentComponentClientState state = (AudioTreatmentComponentClientState) clientState;

	if (state == null) {
	    state = new AudioTreatmentComponentClientState();

	    state.groupId = groupId;
	    state.treatments = treatments;
	    state.volume = volume;
	    state.playWhen = playWhen;
	    state.playOnce = playOnce;
	    state.extent = extent;
	    state.useCellBounds = useCellBounds;
	    state.fullVolumeAreaPercent = fullVolumeAreaPercent;
	    state.distanceAttenuated = distanceAttenuated;
	    state.falloff = falloff;
	    state.showBounds = showBounds;
	}

        return super.getClientState(state, clientID, capabilities);
    }

    @Override
    public void setLive(boolean live) {
        super.setLive(live);

	//System.out.println("AudiotTreatmentComponent Set live " + live);

        //ChannelComponentMO channelComponent = (ChannelComponentMO) cellRef.get().getComponent(ChannelComponentMO.class);

        ChannelComponentMO channelComponent = channelRef.get();

        if (live == false) {
            channelComponent.removeMessageReceiver(AudioTreatmentMenuChangeMessage.class);
            channelComponent.removeMessageReceiver(AudioTreatmentRequestMessage.class);
            channelComponent.removeMessageReceiver(AudioVolumeMessage.class);
	    removeProximityListener();

	    //cellRef.get().removeParentChangeListener(this);
	    //System.out.println("Removed parent change listener");
	    
	    cleanup();
            return;
        }

        ComponentMessageReceiverImpl receiver = new ComponentMessageReceiverImpl(cellRef, this);

        channelComponent.addMessageReceiver(AudioTreatmentMenuChangeMessage.class, receiver);
        channelComponent.addMessageReceiver(AudioTreatmentRequestMessage.class, receiver);
        channelComponent.addMessageReceiver(AudioVolumeMessage.class, receiver);

	initialize();
    }

    private void initialize() {
	if (groupId == null || treatments.length == 0) {
	    /*
	     * The AudioTreatmentComponent hasn't been configured yet.
	     */
	    logger.info("Not starting treatment:  groupID " + groupId + " treatments.length " 
		+ treatments.length);

	    return;
	}

        VoiceManager vm = AppContext.getManager(VoiceManager.class);

        TreatmentGroup group = vm.createTreatmentGroup(groupId);
	
        for (int i = 0; i < treatments.length; i++) {
            TreatmentSetup setup = new TreatmentSetup();

	    setup.spatializer = getSpatializer();

            String treatment = treatments[i];

            String treatmentId = CallID.getCallID(cellID);

	    String pattern = "wlcontent://";

            if (treatment.startsWith(pattern)) {
                /*
                 * We need to create a URL
                 */
                String path = treatment.substring(pattern.length());

                URL url;

                try {
                    path = path.replaceAll(" ", "%20");

                    url = new URL(new URL(serverURL), "webdav/content/" + path);

                    treatment = url.toString();
                } catch (MalformedURLException e) {
                    logger.warning("bad url:  " + e.getMessage());
                    return;
		}
	    } else {
	        pattern = "wls://";

	        if (treatment.startsWith(pattern)) {
                    /*
                     * We need to create a URL from wls:<module>/path
                     */
                    treatment = treatment.substring(pattern.length());  // skip past wls://

                    int ix = treatment.indexOf("/");

                    if (ix < 0) {
                        logger.warning("Bad treatment:  " + treatments[i]);
                        continue;
                    }

                    String moduleName = treatment.substring(0, ix);

                    String path = treatment.substring(ix + 1);

                    logger.fine("Module:  " + moduleName + " treatment " + treatment);

                    URL url;

                    try {
			path = path.replaceAll(" ", "%20");

                        url = new URL(new URL(serverURL),
                            "webdav/content/modules/installed/" + moduleName + "/audio/" + path);

                        treatment = url.toString();
                        logger.fine("Treatment: " + treatment);
                    } catch (MalformedURLException e) {
                        logger.warning("bad url:  " + e.getMessage());
                        continue;
                    }
                }
	    }

            setup.treatment = treatment;

	    vm.addCallStatusListener(this, treatmentId);

            if (setup.treatment == null || setup.treatment.length() == 0) {
                logger.warning("Invalid treatment '" + setup.treatment + "'");
                continue;
            }

            Vector3f location = cellRef.get().getLocalTransform(null).getTranslation(null);

            setup.x = location.getX();
            setup.y = location.getY();
            setup.z = location.getZ();

            logger.info("Starting treatment " + setup.treatment + " at (" + setup.x 
		+ ":" + setup.y + ":" + setup.z + ")");

            try {
		Treatment t = vm.createTreatment(treatmentId, setup);
                group.addTreatment(t);

		if (playWhen.equals(PlayWhen.ALWAYS) == false) {
		    t.pause(true);
		}

	        if (playWhen.equals(PlayWhen.FIRST_IN_RANGE)) {
	            addProximityListener(t);
	        }
            } catch (IOException e) {
                logger.warning("Unable to create treatment " + setup.treatment + e.getMessage());
                return;
            }
        }
    }

    public void setSpatializer(boolean inConeOfSilence) {
	String callID = CallID.getCallID(cellID);

	Player player = AppContext.getManager(VoiceManager.class).getPlayer(callID);
	  
	if (player == null) {
	    logger.warning("no player for " + callID);
	    return;
	}

	if (inConeOfSilence) {
	    CellMO cellMO = cellRef.get();

	    BoundingVolume bounds = cellMO.getLocalBounds();

	    double extent = this.extent;
	    
	    if (bounds instanceof BoundingSphere) {
		float radius = ((BoundingSphere) bounds).getRadius();

		if (useCellBounds) {
		    extent = radius;
		    System.out.println("Using cell bounds " + extent);
		} else {
		    if (extent > radius) {
		        extent = radius;
			System.out.println("Limiting extent to " + extent);
		    }
		} 
	    } else {
		if (useCellBounds) {
		    logger.warning("Only BoundingSphere is supported: " + bounds
		        + " using sphere with radius " + extent);
	        } 
	    }

	    System.out.println("Extent is " + extent);

	    player.setPublicSpatializer(new FullVolumeSpatializer(extent));
	} else {
	    player.setPublicSpatializer(getSpatializer());
	}
    }

    private Spatializer getSpatializer() {
	float cellRadius = getCellRadius();

	double fullVolumeRadius = fullVolumeAreaPercent / 100. * cellRadius;

	double falloff = .92 + ((50 - this.falloff) * ((1 - .92) / 50));

	if (falloff >= 1) {
	    falloff = .999;
	}

	logger.warning("id " + groupId + " cellRadius " + cellRadius 
	    + " extent " + extent + " use cell bounds " + useCellBounds 
	    + " fvr " + fullVolumeRadius + " falloff " 
	    + falloff + " volume " + volume);

	double extent = this.extent;

	/*
	 * TODO if useCellBounds is specified and the bounds type is not
	 * a SPHERE, we will need to add walls to spatialize properly.
	 */
	if (useCellBounds == true) {
	    BoundingVolume boundingVolume = cellRef.get().getLocalBounds();

	    if (boundingVolume instanceof BoundingSphere) {
		extent = ((BoundingSphere) boundingVolume).getRadius();
	    } else {
		logger.warning("Only BoundingSphere is supported: " + boundingVolume
		    + " using sphere with radius " + extent);
	    }
	}

        if (distanceAttenuated == true) {
            DefaultSpatializer spatializer = new DefaultSpatializer();

            spatializer.setFullVolumeRadius(fullVolumeRadius);

	    if (extent == 0) {
                spatializer.setZeroVolumeRadius(cellRadius);
	    } else {
                spatializer.setZeroVolumeRadius(extent);
	    }

	    FalloffFunction falloffFunction = spatializer.getFalloffFunction();

	    falloffFunction.setFalloff(falloff);
	    return spatializer;
        } 

        return new FullVolumeSpatializer(extent);
    }

    private void cleanup() {
	CellMO parent = cellRef.get();

	while (parent != null) {
            ConeOfSilenceComponentMO coneOfSilenceComponentMO = 
	        parent.getComponent(ConeOfSilenceComponentMO.class);

	    if (coneOfSilenceComponentMO != null) {
	        coneOfSilenceComponentMO.removeAudioTreatmentComponentMO(cellRef.get(), this);
		break;
	    } 

	    parent = parent.getParent();
	}

        VoiceManager vm = AppContext.getManager(VoiceManager.class);

        TreatmentGroup group = null;

	if (groupId != null) {
	    group = vm.getTreatmentGroup(groupId);
	}

	if (group == null) {
	    Treatment treatment = vm.getTreatment(CallID.getCallID(cellID));

	    if (treatment == null) {
	  	//System.out.println("No treatment for " + CallID.getCallID(cellID));
		return;
	    }

	    endTreatment(treatment);
	    return;
	}

	Treatment[] treatments = group.getTreatments().values().toArray(new Treatment[0]);

	for (int i = 0; i < treatments.length; i++) {
	    //System.out.println("Ending treatment:  " + treatments[i]);
	    endTreatment(treatments[i]);
	    group.removeTreatment(treatments[i]);
	}

	try {
	    vm.removeTreatmentGroup(group);
	} catch (IOException e) {
	    logger.warning("Unable to remove treatment group " + group);
	}

	vm.dump("all");
    }

    private void endTreatment(Treatment treatment) {
	Call call = treatment.getCall();
	
	if (call == null) {
	    logger.warning("No call for treatment " + treatment);
	    return;
	}

	//System.out.println("Ending call for treatment " + treatment);

	try {
	    call.end(false);
	} catch (IOException e) {
	    logger.warning("Unable to end call " + call + ":  " + e.getMessage());
	}
    }

    public CellMO getCell() {
        return cellRef.get();
    }

    @Override
    protected String getClientClass() {
        return "org.jdesktop.wonderland.modules.audiomanager.client.AudioTreatmentComponent";
    }

    public void parentChanged(CellMO cellMO, CellMO parent) {
	//System.out.println("parent changed... isLive " + isLive());

	if (isLive() == false) {
	    return;
	}

	checkForParentWithCOS();
    }

    private void checkForParentWithCOS() {
	CellMO cellMO = cellRef.get();

	while (cellMO != null) {
            ConeOfSilenceComponentMO coneOfSilenceComponentMO = 
	        cellMO.getComponent(ConeOfSilenceComponentMO.class);

	    if (coneOfSilenceComponentMO != null) {
	        coneOfSilenceComponentMO.addAudioTreatmentComponentMO(cellRef.get(), this);
		break;
	    } 

	    cellMO = cellMO.getParent();
	}
    }

    public void callStatusChanged(CallStatus callStatus) {
        String callId = callStatus.getCallId();

        if (callId == null) {
            logger.warning("No callId in callStatus:  " + callStatus);
            return;
        }

        switch (callStatus.getCode()) {
	case CallStatus.ESTABLISHED:
	    checkForParentWithCOS();
            break;

        case CallStatus.TREATMENTDONE:
	    if (playOnce == true) {
		//System.out.println("TREATMENT DONE");

		channelRef.get().sendAll(null, new AudioTreatmentDoneMessage(cellID, callId));
	    }
	    break;

	case CallStatus.ENDED:
	    break;
        }
    }

    private static class ComponentMessageReceiverImpl extends AbstractComponentMessageReceiver {
        private ManagedReference<AudioTreatmentComponentMO> compRef;

        private CellID cellID;

        public ComponentMessageReceiverImpl(ManagedReference<CellMO> cellRef,
                AudioTreatmentComponentMO comp) {

            super(cellRef.get());

	    cellID = cellRef.get().getCellID();

            compRef = AppContext.getDataManager().createReference(comp);
        }

        public void messageReceived(WonderlandClientSender sender, WonderlandClientID clientID,
                CellMessage message) {

            if (message instanceof AudioTreatmentRequestMessage) {
                AudioTreatmentRequestMessage msg = (AudioTreatmentRequestMessage) message;
                logger.fine("Got AudioTreatmentRequestMessage, startTreatment=" 
		    + msg.restartTreatment());

            	String treatmentId = CallID.getCallID(cellID);

        	Treatment treatment = null;

		treatment = AppContext.getManager(VoiceManager.class).getTreatment(treatmentId);

		if (treatment == null) {
		    logger.warning("Can't find treatment " + treatmentId);
		    return;
		}

		logger.fine("restart " + msg.restartTreatment() + " pause " + msg.isPaused());

		if (msg.restartTreatment()) {
		    treatment.restart(msg.isPaused());
		} else {
		    treatment.pause(msg.isPaused());
		}
                return;
            }

	    if (message instanceof AudioVolumeMessage) {
		handleAudioVolume(sender, clientID, (AudioVolumeMessage) message);
		return;
	    }

            logger.warning("Unknown message:  " + message);
        }

        private void handleAudioVolume(WonderlandClientSender sender, WonderlandClientID clientID,
		AudioVolumeMessage msg) {

	    String softphoneCallID = msg.getSoftphoneCallID();

	    String otherCallID = msg.getOtherCallID();

            double volume = msg.getVolume();

            logger.fine("GOT Volume message:  call " + softphoneCallID
	        + " volume " + volume + " other callID " + otherCallID);

	    logger.fine("GOT Volume message:  call " + softphoneCallID
                + " volume " + volume + " other callID " + otherCallID);

            VoiceManager vm = AppContext.getManager(VoiceManager.class);

            Player softphonePlayer = vm.getPlayer(softphoneCallID);

            if (softphonePlayer == null) {
                logger.warning("Can't find softphone player, callID " + softphoneCallID);
                return;
            }

            Player player = vm.getPlayer(otherCallID);

 	    if (player == null) {
                logger.warning("Can't find player for callID " + otherCallID);
	        return;
            } 

	    if (msg.isSetVolume() == false) {
            	Spatializer spatializer = softphonePlayer.getPrivateSpatializer(player);
	
	 	if (spatializer != null) {
                    msg.setVolume(spatializer.getAttenuator());
		}

                sender.send(clientID, msg);
                logger.fine("Sending vol message " + msg.getVolume());
                return;
            }

	    if (volume == 1.0) {
	        softphonePlayer.removePrivateSpatializer(player);
	        return;
	    }

	    VoiceManagerParameters parameters = vm.getVoiceManagerParameters();

            Spatializer spatializer;

	    spatializer = player.getPublicSpatializer();

	    if (spatializer != null) {
	        spatializer = (Spatializer) spatializer.clone();
	    } else {
	        if (player.getSetup().isLivePlayer) {
		    spatializer = (Spatializer) parameters.livePlayerSpatializer.clone();
	        } else {
		    spatializer = (Spatializer) parameters.stationarySpatializer.clone();
	        }
	    }

            spatializer.setAttenuator(volume);

            softphonePlayer.setPrivateSpatializer(player, spatializer);
	}
    }

    private AudioTreatmentProximityListener proximityListener;

    private void addProximityListener(Treatment treatment) {
        // Fetch the proximity component, we will need this below. If it does
        // not exist (it should), then log an error
        ProximityComponentMO component = cellRef.get().getComponent(ProximityComponentMO.class);

        if (component == null) {
            logger.warning("The AudioTreatment Component does not have a " +
                    "Proximity Component for Cell ID " + cellID);
            return;
        }

        // We are making this component live, add a listener to the proximity component.
	BoundingVolume[] bounds = new BoundingVolume[1];

        bounds[0] = new BoundingSphere(getCellRadius(), new Vector3f());

        AudioTreatmentProximityListener proximityListener = 
	    new AudioTreatmentProximityListener(cellRef.get(), treatment);

        component.addProximityListener(proximityListener, bounds);
    }

    private void removeProximityListener() {
	if (proximityListener != null) {
            ProximityComponentMO component = cellRef.get().getComponent(ProximityComponentMO.class);
	    component.removeProximityListener(proximityListener);
	}
    }

    private float getCellRadius() {
	BoundingVolume bounds = cellRef.get().getLocalBounds();

	logger.warning("Cell bounds:  " + bounds);

	float cellRadius;

	if (bounds instanceof BoundingSphere) {
	    cellRadius = ((BoundingSphere) bounds).getRadius();
	} else if (bounds instanceof BoundingBox) {
	    Vector3f extent = new Vector3f();
	    extent = ((BoundingBox) bounds).getExtent(extent);

	    cellRadius = getMax(extent);
	} else if (bounds instanceof BoundingCapsule) {
	    cellRadius = ((BoundingCapsule) bounds).getRadius();
	} else if (bounds instanceof OrientedBoundingBox) {
	    Vector3f extent = ((OrientedBoundingBox) bounds).getExtent();
	    cellRadius = getMax(extent);
	} else {
	    cellRadius = 5;
	}

	return cellRadius;
    }

    private float getMax(Vector3f extent) {
	float max = extent.getX();

	if (extent.getY() > max) {
	    max = extent.getY();
	}

	if (extent.getZ() > max) {
	    max = extent.getZ();
	}

	return max;
    }

    /**
     * Asks the web server for the module's checksum information given the
     * unique name of the module and a particular asset type, returns null if
     * the module does not exist or upon some general I/O error.
     * 
     * @param serverURL The base web server URL
     * @param moduleName The unique name of a module
     * @param assetType The name of the asset type (art, audio, client, etc.)
     * @return The checksum information for a module
     */
    public static ChecksumList fetchAssetChecksums(String serverURL,
            String moduleName, String assetType) {

        try {
            /* Open an HTTP connection to the Jersey RESTful service */
            String uriPart = moduleName + "/checksums/get/" + assetType;
            URL url = new URL(new URL(serverURL), ASSET_PREFIX + uriPart);
            logger.fine("fetchAssetChecksums:  " + url.toString());
            return ChecksumList.decode(new InputStreamReader(url.openStream()));
        } catch (java.lang.Exception e) {
            /* Log an error and return null */
            logger.warning("[MODULES] FETCH CHECKSUMS Failed " + e.getMessage());
            return null;
        }
    }

}
