package migway.utils;

import java.lang.reflect.Field;
import java.lang.reflect.TypeVariable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReflectionUtils {
    static protected Logger LOG = LoggerFactory.getLogger(ReflectionUtils.class);
    
    static public void infoDeclaredReflect(Object var)  {
        Class<?> t = var.getClass();
        LOG.info("Object " + t.getSimpleName() + " content is:");
        for (int i = 0; i < t.getDeclaredFields().length; i++)
        {
            Field f = t.getDeclaredFields()[i];
            try {
                LOG.info(String.format("Field %s - value is %s", f, f.get(var)));
            } catch (IllegalArgumentException | IllegalAccessException e) {
                LOG.info(String.format("Field %s - value is not accessible", f));
            }
        }
    }
    
    static public void debugReflect(Object var) {
        Class<?> t = var.getClass();
        LOG.debug(String.format("Description of %s:%n" + "  Modifiers    : %d%n" + "  Package      : %s%n" + "  Name         : %s%n"
                + "  CanonicalName: %s%n" + "  SimpleName   : %s%n" + "  TypeName     : %s", t.toGenericString(), t.getModifiers(),
                t.getPackage(), t.getName(), t.getCanonicalName(), t.getSimpleName(), t.getTypeName()));

        LOG.debug(String.format("-- %d AnnotatedInterfaces", t.getAnnotatedInterfaces().length));
        for (int i = 0; i < t.getAnnotatedInterfaces().length; i++) {
            LOG.debug(String.format("AnnotatedInterfaces[%d]: %s", i, t.getAnnotatedInterfaces()[i]));
            LOG.debug(String.format("AnnotatedInterfaces[%d]: Type = %s", i, t.getAnnotatedInterfaces()[i].getType()));
            LOG.debug(String.format("AnnotatedInterfaces[%d]: Annotations = %d", i, t.getAnnotatedInterfaces()[i].getAnnotations().length));
        }
        LOG.debug(String.format("-- AnnotatedSuperclass: %s", t.getAnnotatedSuperclass()));
        LOG.debug(String.format("-- %d Annotations", t.getAnnotations().length));
        for (int i = 0; i < t.getAnnotations().length; i++) {
            LOG.debug(String.format("Annotations[%d]: %s", i, t.getAnnotations()[i]));
        }
        LOG.debug(String.format("-- ComponentType: %s", t.getComponentType()));
        LOG.debug(String.format("-- %d Constructors", t.getConstructors().length));
        for (int i = 0; i < t.getConstructors().length; i++) {
            LOG.debug(String.format("Constructor[%d] = %s", i, t.getConstructors()[i]));
        }
//        LOG.debug(String.format("-- %d DeclaredAnnotations", t.getDeclaredAnnotations().length));
//        for (int i = 0; i < t.getDeclaredAnnotations().length; i++) {
//            LOG.debug(String.format("DeclaredAnnotations[%d] = %s", i, t.getDeclaredAnnotations()[i]));
//        }
//        LOG.debug(String.format("-- %d DeclaredClasses", t.getDeclaredClasses().length));
//        for (int i = 0; i < t.getDeclaredClasses().length; i++) {
//            LOG.debug(String.format("DeclaredClasses[%d] = %s", i, t.getDeclaredClasses()[i]));
//        }
//        LOG.debug(String.format("-- %d DeclaredConstructors", t.getDeclaredConstructors().length));
//        for (int i = 0; i < t.getDeclaredConstructors().length; i++) {
//            LOG.debug(String.format("DeclaredConstructors[%d] = %s", i, t.getDeclaredConstructors()[i]));
//        }
//        LOG.debug(String.format("-- %d DeclaredFields", t.getDeclaredFields().length));
//        for (int i = 0; i < t.getDeclaredFields().length; i++) {
//            LOG.debug(String.format("DeclaredFields[%d] = %s", i, t.getDeclaredFields()[i]));
//        }
//        LOG.debug(String.format("-- %d DeclaredMethods", t.getDeclaredMethods().length));
//        for (int i = 0; i < t.getDeclaredMethods().length; i++) {
//            LOG.debug(String.format("DeclaredMethods[%d] = %s", i, t.getDeclaredMethods()[i]));
//        }
        LOG.debug(String.format("-- DeclaringClass: %s", t.getDeclaringClass()));
        LOG.debug(String.format("-- EnclosingClass: %s", t.getEnclosingClass()));
        LOG.debug(String.format("-- EnclosingConstructor: %s", t.getEnclosingConstructor()));
        LOG.debug(String.format("-- EnclosingMethod: %s", t.getEnclosingMethod()));
        LOG.debug(String.format("-- EnumConstants: %s", t.getEnumConstants()));
        LOG.debug(String.format("-- %d Fields", t.getFields().length));
        for (int i = 0; i < t.getFields().length; i++) {
            LOG.debug(String.format("Fields[%d] = %s", i, t.getFields()[i]));
        }
        LOG.debug(String.format("-- %d GenericInterfaces", t.getGenericInterfaces().length));
        for (int i = 0; i < t.getGenericInterfaces().length; i++) {
            LOG.debug(String.format("GenericInterfaces[%d] = %s", i, t.getGenericInterfaces()[i]));
        }
        LOG.debug(String.format("-- GenericSuperclass: %s", t.getGenericSuperclass()));
        LOG.debug(String.format("-- %d Interfaces", t.getInterfaces().length));
        for (int i = 0; i < t.getInterfaces().length; i++) {
            LOG.debug(String.format("Interfaces[%d] = %s", i, t.getInterfaces()[i]));
        }
        LOG.debug(String.format("-- %d Methods", t.getMethods().length));
        for (int i = 0; i < t.getMethods().length; i++) {
            LOG.debug(String.format("Methods[%d] = %s", i, t.getMethods()[i]));
        }
        LOG.debug(String.format("-- ProtectionDomain: %s", t.getProtectionDomain()));

        if (t.getSigners() != null) {
            LOG.debug(String.format("-- %d Signers", t.getSigners().length));
            for (int i = 0; i < t.getSigners().length; i++) {
                LOG.debug(String.format("Signers[%d] = %s", i, t.getSigners()[i]));
            }
        }
        LOG.debug(String.format("-- Superclass: %s", t.getSuperclass()));
        LOG.debug(String.format("-- %d TypeParameters", t.getTypeParameters().length));
        for (int i = 0; i < t.getTypeParameters().length; i++) {
            LOG.debug(String.format("TypeParameters[%d] = %s", i, t.getTypeParameters()[i]));
            TypeVariable<?> tv = t.getTypeParameters()[i];
            LOG.debug(String.format("---- %d getAnnotatedBounds", tv.getAnnotatedBounds().length));
            for (int j = 0; j < tv.getAnnotatedBounds().length; j++) {
                LOG.debug(String.format("---- getAnnotatedBounds[%d]: %s", j, tv.getAnnotatedBounds()[j]));
            }
            LOG.debug(String.format("---- %d Bounds", tv.getBounds().length));
            for (int j = 0; j < tv.getBounds().length; j++) {
                LOG.debug(String.format("---- Bounds[%d]: %s", j, tv.getBounds()[j]));
            }
            LOG.debug(String.format("---- GenericDeclaration: %s", tv.getGenericDeclaration()));
            LOG.debug(String.format("---- Name: %s", tv.getName()));
            LOG.debug(String.format("---- TypeName: %s", tv.getTypeName()));
            LOG.debug(String.format("---- %d getAnnotations", tv.getAnnotations().length));
            for (int j = 0; j < tv.getAnnotations().length; j++) {
                LOG.debug(String.format("---- getAnnotations[%d]: %s", j, tv.getAnnotations()[j]));
            }
        }
    }
}
