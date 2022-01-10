package io.github.heldev.verso.grpc.interfaces;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

@Target(TYPE)
@Retention(SOURCE)
public @interface VersoMessage {

	/** fully qualified proto message type
	 * e.g. use `google.protobuf.Timestamp` not `com.google.protobuf.Timestamp`
	 */
	String value();
}
