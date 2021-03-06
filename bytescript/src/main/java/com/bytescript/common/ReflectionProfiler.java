package com.bytescript.common;

import com.bytescript.compiler.BSProfiler;
import com.bytescript.lang.*;
import org.objectweb.asm.Type;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class ReflectionProfiler implements BSProfiler {

    @Override
    public Rename getRename(String name) {
        try {
            Class c = Class.forName(name.replace('/','.'));
            return (Rename) c.getAnnotation(Rename.class);
        } catch (ClassNotFoundException e) {
//            throw new Error("Class not found: " + name);
            return null;
        }
    }

    @Override
    public ByteScript getScriptDef(String name) {
        try {
            Class c = Class.forName(name.replace('/', '.'));
            return (ByteScript) c.getAnnotation(ByteScript.class);
        } catch (ClassNotFoundException e) {
         //   throw new Error("Class not found: " + name);
            return null;
        }
    }

    @Override
    public BField getFieldDef(String owner, String name, String desc) {
        try {
            Class c = Class.forName(owner.replace('/','.'));
            for(Field f : c.getDeclaredFields()) {
                if(f.getName().equals(name))  {
                    String desc0 = Type.getDescriptor(f.getType());
                    if(desc.equals(desc0)) {
                        return f.getAnnotation(BField.class);
                    }
                }
            }
            throw new Error("Field not found: " + owner + "." + name + desc);
        } catch (ClassNotFoundException e) {
//            throw new Error("Class not found: " + owner);
            return null;
        }
    }

    @Override
    public BMethod getMethodDef(String owner, String name, String desc) {
        try {
            Class c = Class.forName(owner.replace('/','.'));
            for(Method m : c.getMethods()) {
                String dec0 = Type.getMethodDescriptor(m);
                if(desc.equals(dec0)) {
                    return m.getAnnotation(BMethod.class);
                }
            }
            for(Method m : c.getDeclaredMethods()) {
                String dec0 = Type.getMethodDescriptor(m);
                if(desc.equals(dec0)) {
                    return m.getAnnotation(BMethod.class);
                }
            }
            throw new Error("Method not found: " + owner + "." + name + desc);
        } catch (ClassNotFoundException e) {
//            throw new Error("Class not found: " + owner + "." + name + desc);
            return null;
        }
    }

    @Override
    public BConstructor getConstructorDef(String owner, String name, String desc) {
        try {
            Class c = Class.forName(owner.replace('/','.'));
            for(Constructor init : c.getConstructors()) {
                String dec0 = Type.getConstructorDescriptor(init);
                if(desc.equals(dec0)) {
                    return (BConstructor) init.getAnnotation(BConstructor.class);
                }
            }
            throw new Error("Constructor not found: " + owner + "." + name + desc);
        } catch (ClassNotFoundException e) {
            throw new Error("Class not found: " + owner + "." + name + desc);
        }
    }

    @Override
    public boolean isStaticField(String owner, String name, String desc) {
        try {
            Class c = Class.forName(owner.replace('/','.'));
            for(Field f : c.getDeclaredFields()) {
                String desc0 = Type.getDescriptor(f.getType());
                if(desc.equals(desc0)) {
                    return Modifier.isStatic(f.getModifiers());
                }
            }
            throw new Error("Field not found: " + owner + "." + name + desc);
        } catch (ClassNotFoundException e) {
            throw new Error("Class not found: " + owner + "." + name + desc);
        }
    }

    @Override
    public boolean isStaticMethod(String owner, String name, String desc) {
        try {
            Class c = Class.forName(owner.replace('/','.'));
            for(Method m : c.getDeclaredMethods()) {
                String dec0 = Type.getMethodDescriptor(m);
                if(desc.equals(dec0)) {
                    return Modifier.isStatic(m.getModifiers());
                }
            }
            throw new Error("Method not found: " + owner + "." + name + desc);
        } catch (ClassNotFoundException e) {
            throw new Error("Class not found: " + owner + "." + name + desc);
        }
    }

}