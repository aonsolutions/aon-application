package com.code.aon.jaas.deployment;

import java.io.ObjectStreamException;
import java.io.Serializable;

/**
 * //TODO [iayerbe] Documéntame!
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 13-may-2004
 * @since 1.0
 *  
 */
public class DeploymentState implements Serializable {

	private static final long serialVersionUID = -8058640123116451493L;

	/**
     * //TODO [iayerbe] Documéntame!
     */
    private static final String XCONSTRUCTED = "CONSTRUCTED";

    /**
     * //TODO [iayerbe] Documéntame!
     */
    private static final String XINIT_WAITING_DEPLOYER = "INIT_WAITING_DEPLOYER";

    /**
     * //TODO [iayerbe] Documéntame!
     */
    private static final String XINIT_HAS_DEPLOYER = "INIT_HAS_DEPLOYER";

    /**
     * //TODO [iayerbe] Documéntame!
     */
    private static final String XINIT_DEPLOYER = "INIT_DEPLOYER";

    /**
     * //TODO [iayerbe] Documéntame!
     */
    private static final String XINITIALIZED = "INITIALIZED";

    /**
     * //TODO [iayerbe] Documéntame!
     */
    private static final String XCREATE_SUBDEPLOYMENTS = "CREATE_SUBDEPLOYMENTS";

    /**
     * //TODO [iayerbe] Documéntame!
     */
    private static final String XCREATE_DEPLOYER = "CREATE_DEPLOYER";

    /**
     * //TODO [iayerbe] Documéntame!
     */
    private static final String XCREATED = "CREATED";

    /**
     * //TODO [iayerbe] Documéntame!
     */
    private static final String XSTART_SUBDEPLOYMENTS = "START_SUBDEPLOYMENTS";

    /**
     * //TODO [iayerbe] Documéntame!
     */
    private static final String XSTART_DEPLOYER = "START_DEPLOYER";

    /**
     * //TODO [iayerbe] Documéntame!
     */
    private static final String XSTARTED = "STARTED";

    /**
     * //TODO [iayerbe] Documéntame!
     */
    private static final String XSTOPPED = "STOPPED";

    /**
     * //TODO [iayerbe] Documéntame!
     */
    private static final String XDESTROYED = "DESTROYED";

    /**
     * //TODO [iayerbe] Documéntame!
     */
    private static final String XFAILED = "FAILED";

    /**
     * //TODO [iayerbe] Documéntame!
     */
    public static final DeploymentState CONSTRUCTED = new DeploymentState(
            XCONSTRUCTED);

    /**
     * //TODO [iayerbe] Documéntame!
     */
    public static final DeploymentState INIT_WAITING_DEPLOYER = new DeploymentState(
            XINIT_WAITING_DEPLOYER);

    /**
     * //TODO [iayerbe] Documéntame!
     */
    public static final DeploymentState INIT_HAS_DEPLOYER = new DeploymentState(
            XINIT_HAS_DEPLOYER);

    /**
     * //TODO [iayerbe] Documéntame!
     */
    public static final DeploymentState INIT_DEPLOYER = new DeploymentState(
            XINIT_DEPLOYER);

    /**
     * //TODO [iayerbe] Documéntame!
     */
    public static final DeploymentState INITIALIZED = new DeploymentState(
            XINITIALIZED);

    /**
     * //TODO [iayerbe] Documéntame!
     */
    public static final DeploymentState CREATE_SUBDEPLOYMENTS = new DeploymentState(
            XCREATE_SUBDEPLOYMENTS);

    /**
     * //TODO [iayerbe] Documéntame!
     */
    public static final DeploymentState CREATE_DEPLOYER = new DeploymentState(
            XCREATE_DEPLOYER);

    /**
     * //TODO [iayerbe] Documéntame!
     */
    public static final DeploymentState CREATED = new DeploymentState(XCREATED);

    /**
     * //TODO [iayerbe] Documéntame!
     */
    public static final DeploymentState START_SUBDEPLOYMENTS = new DeploymentState(
            XSTART_SUBDEPLOYMENTS);

    /**
     * //TODO [iayerbe] Documéntame!
     */
    public static final DeploymentState START_DEPLOYER = new DeploymentState(
            XSTART_DEPLOYER);

    /**
     * //TODO [iayerbe] Documéntame!
     */
    public static final DeploymentState STARTED = new DeploymentState(XSTARTED);

    /**
     * //TODO [iayerbe] Documéntame!
     */
    public static final DeploymentState STOPPED = new DeploymentState(XSTOPPED);

    /**
     * //TODO [iayerbe] Documéntame!
     */
    public static final DeploymentState DESTROYED = new DeploymentState(
            XDESTROYED);

    /**
     * //TODO [iayerbe] Documéntame!
     */
    public static final DeploymentState FAILED = new DeploymentState(XFAILED);

    /**
     * //TODO [iayerbe] Documéntame!
     */
    private String state;

    /**
     * //TODO [iayerbe] Documéntame! Constructor for DeploymentState
     * 
     * @param state
     *            String
     */
    private DeploymentState(String state) {
        this.state = state;
    }

    /**
     * //TODO [iayerbe] Tradúceme! A factory to translate a string into the
     * corresponding DeploymentState.
     * 
     * @param state
     *            String
     * @return DeploymentState
     */
    public static DeploymentState getDeploymentState(String state) {
        DeploymentState theState = null;
        state = state.toUpperCase();
        if (state.equals(XCONSTRUCTED)) {
            theState = CONSTRUCTED;
        } else if (state.equals(XINIT_WAITING_DEPLOYER)) {
            theState = INIT_WAITING_DEPLOYER;
        } else if (state.equals(XINIT_HAS_DEPLOYER)) {
            theState = INIT_HAS_DEPLOYER;
        } else if (state.equals(XINIT_DEPLOYER)) {
            theState = INIT_DEPLOYER;
        } else if (state.equals(XINITIALIZED)) {
            theState = INITIALIZED;
        } else if (state.equals(XCREATE_SUBDEPLOYMENTS)) {
            theState = CREATE_SUBDEPLOYMENTS;
        } else if (state.equals(XCREATE_DEPLOYER)) {
            theState = CREATE_DEPLOYER;
        } else if (state.equals(XCREATED)) {
            theState = CREATED;
        } else if (state.equals(XSTART_SUBDEPLOYMENTS)) {
            theState = START_SUBDEPLOYMENTS;
        } else if (state.equals(XSTART_DEPLOYER)) {
            theState = START_DEPLOYER;
        } else if (state.equals(XSTARTED)) {
            theState = STARTED;
        } else if (state.equals(XSTOPPED)) {
            theState = STOPPED;
        } else if (state.equals(XDESTROYED)) {
            theState = DESTROYED;
        } else if (state.equals(XFAILED)) {
            theState = FAILED;
        }

        return theState;
    }

    /**
     * //TODO [iayerbe] Documéntame! Method toString
     * 
     * @return String
     */
    public String toString() {
        return state;
    }

    /**
     * //TODO [iayerbe] Tradúceme! Resolve objects on deserialization to one of
     * the identity objects.
     * 
     * @return Object
     * @throws ObjectStreamException
     */
    private Object readResolve() throws ObjectStreamException {
        Object identity = getDeploymentState(state);
        return identity;
    }

}