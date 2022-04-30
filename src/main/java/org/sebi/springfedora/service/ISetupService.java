package org.sebi.springfedora.service;

public interface ISetupService {
    /**
     * Creates the basic fedora6 setup for GAMS.
     * Like creations of aggregations / objects etc. containers
     */
    public void createBaseResources();

    /**
     * Creates prototype digital objects for GAMS
     */
    public void createPrototypes();
}
