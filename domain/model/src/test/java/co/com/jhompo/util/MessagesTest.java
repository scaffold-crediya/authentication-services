package co.com.jhompo.util;

import org.junit.jupiter.api.Test;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class MessagesTest {

    @Test
    void testPrivateConstructorsAndConstants() {
        // Testea el constructor principal y asegura que no se puede instanciar
        try {
            Constructor<Messages> constructor = Messages.class.getDeclaredConstructor();
            assertTrue(Modifier.isPrivate(constructor.getModifiers()));
            constructor.setAccessible(true);
            constructor.newInstance();
            fail("Se esperaba una excepción de estado ilegal");
        } catch (InvocationTargetException e) {
            assertTrue(e.getCause() instanceof IllegalStateException);
        } catch (Exception e) {
            fail("Error inesperado: " + e.getMessage());
        }

        // Accede a una constante de cada clase interna para cubrir el código
        assertNotNull(Messages.JWT.BEARER);
        assertNotNull(Messages.USER.CREATED_SUCCESS);
        assertNotNull(Messages.ROLE.CREATED_SUCCESS);
        assertNotNull(Messages.PERMISSION.ASSIGNED_SUCCESS);
        assertNotNull(Messages.SYSTEM.OPERATION_SUCCESS);
        assertNotNull(Messages.REQUEST.CREATED_SUCCESS);
        assertNotNull(Messages.HTTP.CODE_200);
    }
}