package com.givemefive.gmfcontroller.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.StreamReadConstraints;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.givemefive.gmfcontroller.common.helper.HotPropertiesHelper;
import org.apache.commons.lang3.StringUtils;

/**
 * 
 * @author guosk
 *
 */
public final class JsonHelper {

  private static final boolean DEFAULT_USE_SNAKE = true;

  /**
   * Jackson Object Mapper used to serialization/deserialization
   */
  private static JsonMapper objectMapper;

  /**
   *  Naming convention between camel and underscores.
   */
  private static JsonMapper objectMapperSnake;

  private JsonHelper() {
    throw new IllegalStateException("Utility class");
  }

  private static void initialize(boolean snake) {
    JsonMapper mapper = JsonMapper.builder().disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .disable(MapperFeature.DEFAULT_VIEW_INCLUSION)
        .enable(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS)
        .enable(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER).build();
    mapper.registerModules(new JavaTimeModule(), new SimpleModule());
    mapper.setAnnotationIntrospector(new JacksonAnnotationIntrospector());
    String maxStringLength = HotPropertiesHelper.getProperty("spring.jackson.max-string-length");
    if (StringUtils.isNotBlank(maxStringLength)) {
      mapper.getFactory().setStreamReadConstraints(StreamReadConstraints.builder()
          .maxStringLength(Integer.parseInt(maxStringLength)).build());
    }

    if (snake) {
      mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
      objectMapperSnake = mapper;
    } else {
      objectMapper = mapper;
    }
  }

  /**
   * Return the ObjectMapper. It can be used to customize
   * serialization/deserialization configuration.
   * 
   * @return
   */
  public static ObjectMapper getObjectMapper(boolean snake) {
    if (snake && objectMapperSnake == null) {
      initialize(true);
    } else if (!snake && objectMapper == null) {
      initialize(false);
    }
    if (snake) {
      return objectMapperSnake;
    } else {
      return objectMapper;
    }
  }

  /**
   * Serialize and object to a JSON String representation
   * 
   * @param o
   *            The object to serialize
   * @return The JSON String representation
   */
  public static String serialize(Object o, boolean snake) {
    ObjectMapper mapper = getObjectMapper(snake);
    Writer writer = new StringWriter();
    try {
      mapper.writeValue(writer, o);
    } catch (IOException e) {
      throw new SerializationException(e);
    }
    return writer.toString();
  }

  public static String serialize(Object o) {
    return serialize(o, DEFAULT_USE_SNAKE);
  }

  /**
   * 
   * @param o
   * @param writer
   */
  public static void serialize2Writer(Object o, Writer writer, boolean snake) {
    ObjectMapper mapper = getObjectMapper(snake);
    try {
      mapper.writeValue(writer, o);
    } catch (IOException e) {
      throw new SerializationException(e);
    }
  }

  public static void serialize2Writer(Object o, Writer writer) {
    serialize2Writer(o, writer, DEFAULT_USE_SNAKE);
  }

  /**
   * Serialize and object to a JSON String representation with a Jackson view
   * 
   * @param o The object to serialize
   * @param view The Jackson view to use
   * @return The JSON String representation
   */
  public static String serialize(Object o, Class<?> view, boolean snake) {
    ObjectMapper mapper = getObjectMapper(snake);
    Writer w = new StringWriter();
    try {
      ObjectWriter writer = mapper.writerWithView(view);
      writer.writeValue(w, o);
    } catch (IOException e) {
      throw new SerializationException(e);
    }
    return w.toString();
  }

  public static String serialize(Object o, Class<?> view) {
    return serialize(o, view, DEFAULT_USE_SNAKE);
  }

  /**
   * Deserialize a JSON string
   * 
   * @param content The JSON String object representation
   * @param type The type of the deserialized object instance
   * @return The deserialized object instance
   */
  public static <T> T deserialize(String content, Class<T> type, boolean snake) {
    ObjectMapper mapper = getObjectMapper(snake);
    try {
      return mapper.readValue(content, type);
    } catch (IOException e) {
      throw new UnsupportedOperationException(e);
    }
  }

  public static <T> T deserialize(String content, Class<T> type) {
    return deserialize(content, type, DEFAULT_USE_SNAKE);
  }

  public static <T> T deserialize(InputStream inputStream, Class<T> type, boolean snake) {
    ObjectMapper mapper = getObjectMapper(snake);
    try {
      return mapper.readValue(inputStream, type);
    } catch (IOException e) {
      throw new UnsupportedOperationException(e);
    }
  }


  public static <T> T deserialize(InputStream inputStream, Class<T> type) {
    return deserialize(inputStream, type, DEFAULT_USE_SNAKE);
  }

  /**
   * Deserialize a JSON string
   * 
   * @param content The JSON String object representation
   * @param valueTypeRef The typeReference containing the type of the deserialized object instance
   * @return The deserialized object instance
   */
  @SuppressWarnings("rawtypes")
  public static <T> T deserialize(String content, TypeReference valueTypeRef, boolean snake) {
    ObjectMapper mapper = getObjectMapper(snake);
    try {
      return (T) mapper.readValue(content, valueTypeRef);
    } catch (IOException e) {
      throw new UnsupportedOperationException(e);
    }
  }

  @SuppressWarnings("rawtypes")
  public static <T> T deserialize(String content, TypeReference valueTypeRef) {
    return deserialize(content, valueTypeRef, DEFAULT_USE_SNAKE);
  }

  @SuppressWarnings("rawtypes")
  public static <T> T deserialize(InputStream inputStream, TypeReference valueTypeRef,
      boolean snake) {
    ObjectMapper mapper = getObjectMapper(snake);
    try {
      return (T) mapper.readValue(inputStream, valueTypeRef);
    } catch (IOException e) {
      throw new UnsupportedOperationException(e);
    }
  }

  @SuppressWarnings("rawtypes")
  public static <T> T deserialize(InputStream inputStream, TypeReference valueTypeRef) {
    return deserialize(inputStream, valueTypeRef, DEFAULT_USE_SNAKE);
  }

  @SuppressWarnings("rawtypes")
  public static <T> T deserialize(JsonNode jsonNode, TypeReference valueTypeRef, boolean snake) {
    ObjectMapper mapper = getObjectMapper(snake);
    try {
      return (T) mapper.convertValue(jsonNode, valueTypeRef);
    } catch (IllegalArgumentException e) {
      throw new UnsupportedOperationException(e);
    }
  }

  @SuppressWarnings("rawtypes")
  public static <T> T deserialize(JsonNode jsonNode, TypeReference valueTypeRef) {
    return deserialize(jsonNode, valueTypeRef, DEFAULT_USE_SNAKE);
  }

  public static JsonNode parseJson(String content, boolean snake) {
    ObjectMapper mapper = getObjectMapper(snake);
    try {
      return mapper.readValue(content, JsonNode.class);
    } catch (IOException e) {
      throw new UnsupportedOperationException(e);
    }
  }

  public static JsonNode parseJson(String content) {
    return parseJson(content, DEFAULT_USE_SNAKE);
  }

  public static class SerializationException extends RuntimeException {

    public SerializationException(Throwable cause) {
      super(cause);
    }
  }

  /**
   * 序列化null对象为空对象
   */
  public static class NullObjectSerializer extends JsonSerializer<Object> {

    @Override
    public void serialize(Object value, JsonGenerator jsonGenerator, SerializerProvider serializers)
        throws IOException {
      jsonGenerator.writeObject(Objects.requireNonNullElseGet(value, Object::new));
    }
  }

  /**
   * 序列化null数组为空数组
   */
  public static class NullArraySerializer extends JsonSerializer<Object> {

    @Override
    public void serialize(Object value, JsonGenerator jsonGenerator, SerializerProvider serializers)
        throws IOException {
      if (value == null) {
        jsonGenerator.writeStartArray();
        jsonGenerator.writeEndArray();
      } else {
        jsonGenerator.writeObject(value);
      }
    }
  }
}
