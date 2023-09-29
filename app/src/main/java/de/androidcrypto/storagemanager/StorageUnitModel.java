package de.androidcrypto.storagemanager;

public class StorageUnitModel {
    // version 1

    private String unitId; // this is internally used by database
    private String unitIdServer; // the unitId on the server
    private String unitNumber; // number by user, e.g. B001, should be unique
    private String unitShortContent; // the content of the unit in one line
    private String unitContent; // the content of the unit
    private String unitType; // e.g. cardboard or plasticbox or single
    private String unitWeight; // free format like light or heavy
    private String unitPlace; // free format like last row 3rd from down
    private String unitRoomNr; // the room number where the unit is stored, e.g. M
    private String unitLastEdit; // timestampe lik 2023-09-29
    private String unitTagUid1, unitTagUid2, unitTagUid3; // the NFC tag UID, 14 character hex characters like 0123456789abcdef
    private String unitImageFilename1, unitImageFilename2, unitImageFilename3;
    private String unitDeleted; // true or false as string

    public StorageUnitModel(String unitId, String unitNumber, String unitShortContent, String unitContent, String unitType, String unitWeight, String unitPlace, String unitRoomNr) {
        this.unitId = unitId;
        this.unitNumber = unitNumber;
        this.unitShortContent = unitShortContent;
        this.unitContent = unitContent;
        this.unitType = unitType;
        this.unitWeight = unitWeight;
        this.unitPlace = unitPlace;
        this.unitRoomNr = unitRoomNr;
    }

    public StorageUnitModel(String unitId, String unitIdServer, String unitNumber, String unitTagUid1, String unitTagUid2, String unitTagUid3, String unitShortContent, String unitContent, String unitRoomNr, String unitType, String unitPlace, String unitLastEdit, String unitWeight) {
        this.unitId = unitId;
        this.unitIdServer = unitIdServer;
        this.unitNumber = unitNumber;
        this.unitTagUid1 = unitTagUid1;
        this.unitTagUid2 = unitTagUid2;
        this.unitTagUid3 = unitTagUid3;
        this.unitShortContent = unitShortContent;
        this.unitContent = unitContent;
        this.unitRoomNr = unitRoomNr;
        this.unitType = unitType;
        this.unitPlace = unitPlace;
        this.unitLastEdit = unitLastEdit;
        this.unitWeight = unitWeight;
    }

    public StorageUnitModel(String unitId, String unitIdServer, String unitNumber) {
        this.unitId = unitId;
        this.unitIdServer = unitIdServer;
        this.unitNumber = unitNumber;
    }

    // constructor for reading all units
    public StorageUnitModel(String unitId, String unitIdServer, String unitNumber, String unitShortContent, String unitContent,
                            String unitType, String unitWeight, String unitPlace, String unitRoomNr, String unitLastEdit,
                            String unitTagUid1, String unitTagUid2, String unitTagUid3,
                            String unitImageFilename1, String unitImageFilename2, String unitImageFilename3, String unitDeleted) {
        this.unitId = unitId;
        this.unitIdServer = unitIdServer;
        this.unitNumber = unitNumber;
        this.unitShortContent = unitShortContent;
        this.unitContent = unitContent;
        this.unitType = unitType;
        this.unitWeight = unitWeight;
        this.unitPlace = unitPlace;
        this.unitRoomNr = unitRoomNr;
        this.unitLastEdit = unitLastEdit;
        this.unitTagUid1 = unitTagUid1;
        this.unitTagUid2 = unitTagUid2;
        this.unitTagUid3 = unitTagUid3;
        this.unitImageFilename1 = unitImageFilename1;
        this.unitImageFilename2 = unitImageFilename2;
        this.unitImageFilename3 = unitImageFilename3;
        this.unitDeleted = unitDeleted;
    }

    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    public String getUnitIdServer() {
        return unitIdServer;
    }

    public void setUnitIdServer(String unitIdServer) {
        this.unitIdServer = unitIdServer;
    }

    public String getUnitNumber() {
        return unitNumber;
    }

    public void setUnitNumber(String unitNumber) {
        this.unitNumber = unitNumber;
    }

    public String getUnitTagUid1() {
        return unitTagUid1;
    }

    public void setUnitTagUid1(String unitTagUid1) {
        this.unitTagUid1 = unitTagUid1;
    }

    public String getUnitTagUid2() {
        return unitTagUid2;
    }

    public void setUnitTagUid2(String unitTagUid2) {
        this.unitTagUid2 = unitTagUid2;
    }

    public String getUnitTagUid3() {
        return unitTagUid3;
    }

    public void setUnitTagUid3(String unitTagUid3) {
        this.unitTagUid3 = unitTagUid3;
    }

    public String getUnitShortContent() {
        return unitShortContent;
    }

    public void setUnitShortContent(String unitShortContent) {
        this.unitShortContent = unitShortContent;
    }

    public String getUnitContent() {
        return unitContent;
    }

    public void setUnitContent(String unitContent) {
        this.unitContent = unitContent;
    }

    public String getUnitRoomNr() {
        return unitRoomNr;
    }

    public void setUnitRoomNr(String unitRoomNr) {
        this.unitRoomNr = unitRoomNr;
    }

    public String getUnitType() {
        return unitType;
    }

    public void setUnitType(String unitType) {
        this.unitType = unitType;
    }

    public String getUnitPlace() {
        return unitPlace;
    }

    public void setUnitPlace(String unitPlace) {
        this.unitPlace = unitPlace;
    }

    public String getUnitLastEdit() {
        return unitLastEdit;
    }

    public void setUnitLastEdit(String unitLastEdit) {
        this.unitLastEdit = unitLastEdit;
    }

    public String getUnitWeight() {
        return unitWeight;
    }

    public void setUnitWeight(String unitWeight) {
        this.unitWeight = unitWeight;
    }

    public String getUnitImageFilename1() {
        return unitImageFilename1;
    }

    public void setUnitImageFilename1(String unitImageFilename1) {
        this.unitImageFilename1 = unitImageFilename1;
    }

    public String getUnitImageFilename2() {
        return unitImageFilename2;
    }

    public void setUnitImageFilename2(String unitImageFilename2) {
        this.unitImageFilename2 = unitImageFilename2;
    }

    public String getUnitImageFilename3() {
        return unitImageFilename3;
    }

    public void setUnitImageFilename3(String unitImageFilename3) {
        this.unitImageFilename3 = unitImageFilename3;
    }

    public String getUnitDeleted() {
        return unitDeleted;
    }

    public void setUnitDeleted(String unitDeleted) {
        this.unitDeleted = unitDeleted;
    }
}
