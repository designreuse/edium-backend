package com.edium.library.payload;

public class EntryGrantRequest {
    private boolean readGrant;
    private boolean writeGrant;
    private boolean deleteGrant;
    private boolean inherit;

    public boolean isReadGrant() {
        return readGrant;
    }

    public void setReadGrant(boolean readGrant) {
        this.readGrant = readGrant;
    }

    public boolean isWriteGrant() {
        return writeGrant;
    }

    public void setWriteGrant(boolean writeGrant) {
        this.writeGrant = writeGrant;
    }

    public boolean isDeleteGrant() {
        return deleteGrant;
    }

    public void setDeleteGrant(boolean deleteGrant) {
        this.deleteGrant = deleteGrant;
    }

    public boolean isInherit() {
        return inherit;
    }

    public void setInherit(boolean inherit) {
        this.inherit = inherit;
    }
}
