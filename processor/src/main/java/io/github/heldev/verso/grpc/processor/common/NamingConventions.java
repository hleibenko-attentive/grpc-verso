package io.github.heldev.verso.grpc.processor.common;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

public abstract class NamingConventions {
	private NamingConventions() {
	}

	/**
	 * <a href="https://github.com/protocolbuffers/protobuf/blob/master/src/google/protobuf/compiler/java/java_helpers.cc"
	 * 		>proto conversion algorithm</a>
	 */
	public static String getProtobufGetterSuffix(String protoFieldName) {
		//todo check digits
		return Stream.of(protoFieldName.split("_"))
				.map(NamingConventions::capitalize)
				.collect(joining());
	}

	private static String capitalize(String s) {
		return IntStream.concat(s.chars().limit(1).map(NamingConventions::latinToUpperCase), s.chars().skip(1))
				.collect(StringBuilder::new, (builder, c) -> builder.append((char) c), StringBuilder::append)
				.toString();
	}

	private static char latinToUpperCase(int c) {
		int codePoint = 'a' <= c && c <= 'z' ? Character.toUpperCase(c) : c;
		return (char) codePoint;
	}

	public static String toSanitizedCapitalCamelCase(String text) {
		return Stream.of(text.split("[^\\p{Alnum}]"))
				.map(NamingConventions::capitalizeFirstLetter)
				.collect(joining());
	}

	private static String capitalizeFirstLetter(String text) {
		return text.isEmpty()
				? text
				: Character.toUpperCase(text.charAt(0)) + text.substring(1);
	}
}
