/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.seed.core.internal.transaction;

import org.junit.Test;
import org.seedstack.seed.transaction.Transactional;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.assertj.core.api.Assertions.assertThat;

public class TransactionalAnnotationTest {
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE, ElementType.METHOD})
    @Transactional
    private @interface MetaTransactional {
    }

    @MetaTransactional
    static class MetaTransactionalClass {
        public void someMethod() {

        }
    }

    @Transactional
    static class TransactionalClass {
        public void someMethod() {

        }
    }

    static class NotTransactionalClass {
        @Transactional
        public void transactionalMethod() {

        }

        @MetaTransactional
        public void metaTransactionalMethod() {

        }
    }

    @Transactional
    interface TransactionalInterface {
        void someMethod();
    }

    interface NotTransactionalInterface {
        @Transactional
        void transactionalMethod();
    }

    static class InterfaceMethodInherited implements NotTransactionalInterface {
        @Override
        public void transactionalMethod() {

        }

        public void localMethod() {

        }
    }

    static class InterfaceInherited implements TransactionalInterface {
        @Override
        public void someMethod() {

        }

        public void localMethod() {

        }
    }

    static class ClassMethodInherited extends NotTransactionalClass {
        public void transactionalMethod() {

        }

        public void localMethod() {

        }
    }

    static class ClassInherited extends TransactionalClass {
        public void someMethod() {

        }

        public void localMethod() {

        }
    }


    @Test
    public void metaAnnotation() throws Exception {
        assertThat(TransactionPlugin.TRANSACTIONAL_MATCHER.matches(MetaTransactionalClass.class.getMethod("someMethod"))).isTrue();
        assertThat(TransactionPlugin.TRANSACTIONAL_MATCHER.matches(NotTransactionalClass.class.getMethod("metaTransactionalMethod"))).isTrue();
    }

    @Test
    public void annotationOnMethod() throws Exception {
        assertThat(TransactionPlugin.TRANSACTIONAL_MATCHER.matches(NotTransactionalClass.class.getMethod("transactionalMethod"))).isTrue();
        assertThat(TransactionPlugin.TRANSACTIONAL_MATCHER.matches(NotTransactionalInterface.class.getMethod("transactionalMethod"))).isTrue();
    }

    @Test
    public void annotationOnClass() throws Exception {
        assertThat(TransactionPlugin.TRANSACTIONAL_MATCHER.matches(TransactionalClass.class.getMethod("someMethod"))).isTrue();
        assertThat(TransactionPlugin.TRANSACTIONAL_MATCHER.matches(TransactionalInterface.class.getMethod("someMethod"))).isTrue();
    }

    @Test
    public void annotationOnInheritedInterfaceMethod() throws Exception {
        assertThat(TransactionPlugin.TRANSACTIONAL_MATCHER.matches(InterfaceMethodInherited.class.getMethod("transactionalMethod"))).isTrue();
        assertThat(TransactionPlugin.TRANSACTIONAL_MATCHER.matches(InterfaceMethodInherited.class.getMethod("localMethod"))).isFalse();
    }

    @Test
    public void annotationOnInheritedInterface() throws Exception {
        assertThat(TransactionPlugin.TRANSACTIONAL_MATCHER.matches(InterfaceInherited.class.getMethod("someMethod"))).isTrue();
        assertThat(TransactionPlugin.TRANSACTIONAL_MATCHER.matches(InterfaceInherited.class.getMethod("localMethod"))).isFalse();
    }

    @Test
    public void annotationOnInheritedClassMethod() throws Exception {
        assertThat(TransactionPlugin.TRANSACTIONAL_MATCHER.matches(ClassMethodInherited.class.getMethod("transactionalMethod"))).isTrue();
        assertThat(TransactionPlugin.TRANSACTIONAL_MATCHER.matches(ClassMethodInherited.class.getMethod("localMethod"))).isFalse();
    }

    @Test
    public void annotationOnInheritedClass() throws Exception {
        assertThat(TransactionPlugin.TRANSACTIONAL_MATCHER.matches(ClassInherited.class.getMethod("someMethod"))).isTrue();
        assertThat(TransactionPlugin.TRANSACTIONAL_MATCHER.matches(ClassInherited.class.getMethod("localMethod"))).isTrue();
    }
}
