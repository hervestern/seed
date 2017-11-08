package org.seedstack.seed.transaction;

public interface Transaction {
    void commit();

    void rollback();
}
