package com.fasterxml.jackson.module.scala.deser

import org.codehaus.jackson.`type`.JavaType
import org.codehaus.jackson.map._
import com.fasterxml.jackson.module.scala.JacksonModule
import org.codehaus.jackson.{JsonToken, JsonParser}

private class EnumerationDeserializer(`type`:JavaType) extends JsonDeserializer[Object] {
	override def deserialize(jp:JsonParser, ctxt:DeserializationContext) = {
		if (jp.getCurrentToken != JsonToken.START_OBJECT)
			throw ctxt.mappingException(`type`.getRawClass)
		val (eclass,eclassName) = parsePair(jp)
		if (eclass != "enumClass")
			throw ctxt.mappingException(`type`.getRawClass)
		val (value, valueValue) = parsePair(jp)
		if (value != "value")
			throw ctxt.mappingException(`type`.getRawClass)
		jp.nextToken()
		Class.forName(eclassName).getMethod("withName",classOf[String]).invoke(null,valueValue)
	}

	private def parsePair( jp:JsonParser ) = ({jp.nextToken; jp.getText}, {jp.nextToken; jp.getText})
}

private object EnumerationDeserializerResolver extends Deserializers.Base {

  override def findBeanDeserializer(javaType: JavaType,
							 config: DeserializationConfig,
							 provider: DeserializerProvider,
							 beanDesc: BeanDescription,
							 property: BeanProperty) = {

		val clazz = javaType.getRawClass
		var deserializer : JsonDeserializer[_] = null;

		if (classOf[scala.Enumeration$Value].isAssignableFrom(clazz)) {
			deserializer = new EnumerationDeserializer(javaType)
		}

		deserializer
	}

}

trait EnumerationDeserializerModule extends JacksonModule {
  this += EnumerationDeserializerResolver
}
