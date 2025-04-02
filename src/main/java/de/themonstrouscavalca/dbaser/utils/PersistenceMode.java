package de.themonstrouscavalca.dbaser.utils;

public enum PersistenceMode{
    EXPECT_RESULT(true),
    NO_RESULT_EXPECTED(false);

    private final boolean expectResultValue;

    PersistenceMode(boolean expectResultValue){
        this.expectResultValue = expectResultValue;
    }

    public boolean isExpected(){
        return expectResultValue;
    }
}
