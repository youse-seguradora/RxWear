package com.patloew.rxwear;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import rx.exceptions.Exceptions;

/* Copyright 2016 Patrick Löwenstein
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. */
public class IOUtil {

    @SuppressWarnings("unchecked")
    public static <T> T readObjectFromByteArray(byte[] data) throws RuntimeException {
        ObjectInputStream ois = null;

        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            ois = new ObjectInputStream(bais);
            return (T) ois.readObject();

        } catch(Exception e) {
            throw Exceptions.propagate(e);

        } finally {
            closeSilently(ois);
        }
    }

    public static byte[] writeObjectToByteArray(Serializable serializable) throws IOException {
        ObjectOutputStream oos = null;

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(out);
            oos.writeObject(serializable);
            oos.flush();
            return out.toByteArray();
        } finally {
            IOUtil.closeSilently(oos);
        }
    }

    public static void closeSilently(Closeable closable) {
        try {
            if(closable != null) { closable.close(); }
        } catch(IOException ignore) { }
    }
}
