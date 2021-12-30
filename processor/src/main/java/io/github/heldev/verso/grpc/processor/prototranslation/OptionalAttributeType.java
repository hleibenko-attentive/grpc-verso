package io.github.heldev.verso.grpc.processor.prototranslation;

public enum OptionalAttributeType {
	/** example: builder.setAttribute(Type object) */
	VALUE_SETTER,

	/** example: builder.setAttribute(Optional<Type> object) */
	OPTIONAL_SETTER,

	VALUE_AND_OPTIONAL_SETTERS
}
