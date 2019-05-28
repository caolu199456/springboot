package com.example.util.pool.string;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

public class StringBufferFactory extends BasePooledObjectFactory<StringBuffer> {
    @Override
    public StringBuffer create() throws Exception {
        return new StringBuffer();
    }

    @Override
    public PooledObject<StringBuffer> wrap(StringBuffer obj) {
        return new DefaultPooledObject<>(obj);
    }

    @Override
    public boolean validateObject(PooledObject<StringBuffer> p) {
        return p.getObject() != null;
    }

    @Override
    public void passivateObject(PooledObject<StringBuffer> p) throws Exception {
        p.getObject().setLength(0);
    }
}
