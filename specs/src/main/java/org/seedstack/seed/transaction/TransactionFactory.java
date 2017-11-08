package org.seedstack.seed.transaction;

public interface TransactionFactory {
    <T extends Transaction> T begin();
}
