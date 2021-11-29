package nl.iobyte.nodalcommunication.interfaces;

public interface ISerializer {

    /**
     * Object to String
     * @param obj Object
     * @return byte[]
     */
    String serialize(Object obj);

    /**
     * String to Object
     * @param bytes byte[]
     * @param clazz Class<T>
     * @param <T> T
     * @return T
     */
    <T> T deserialize(byte[] bytes, Class<T> clazz);

}
