package ru.job4j.domain;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

public interface Patchable<T> {

    default void patch(T dataToSet)
            throws IllegalAccessException, InvocationTargetException {
        HashMap<String, Method> namePerMethod = getGettersAndSetters();
        for (String name : namePerMethod.keySet()) {
            if (name.startsWith("get")) {
                Method getMethod = namePerMethod.get(name);
                Method setMethod = namePerMethod.get(name.replace("get", "set"));
                if (setMethod == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Impossible invoke set method from object : " + this
                                    + ", Check set and get pairs.");
                }
                var newValue = getMethod.invoke(dataToSet);
                if (newValue != null) {
                    setMethod.invoke(this, newValue);
                }
            }
        }
    }

    private HashMap<String, Method> getGettersAndSetters() {
        Method[] methods = this.getClass().getDeclaredMethods();
        HashMap<String, Method> namePerMethod = new HashMap<>();
        for (Method method : methods) {
            var name = method.getName();
            if (name.startsWith("get") || name.startsWith("set")) {
                namePerMethod.put(name, method);
            }
        }
        return namePerMethod;
    }
}
