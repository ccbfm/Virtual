package com.virtual.util.persist.model;

public class VPersistModel extends VModel<VPersistModel> {

    public String name;

    public int valueInt;

    public String valueString;

    public Integer integer;

    public VPersistModel persistModel;

    @Override
    protected VPersistModel getModel() {
        return this;
    }
}
