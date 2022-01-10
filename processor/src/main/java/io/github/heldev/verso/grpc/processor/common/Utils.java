package io.github.heldev.verso.grpc.processor.common;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.lang.String.format;

public abstract class Utils {
	private Utils() {
	}

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	public static <T> Stream<T> optionalToStream(Optional<T> optional) {
		return optional.map(Stream::of).orElseGet(Stream::empty);
	}

	public static <T> Optional<T> optionalWhen(boolean condition, T value) {
		return condition
				? Optional.of(value)
				: Optional.empty();
	}

	public static <T> Optional<T> optionalWhen(boolean condition, Supplier<T> supplier) {
		return condition
				? Optional.of(supplier.get())
				: Optional.empty();
	}

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	public static <T> Optional<T> optionalOr(Optional<T> a, Optional<T> b) {
		return a.isPresent()
				? a
				: b;
	}

	/** always throws, return type here is only for type checking */
	public static RuntimeException panic(String template, Object... arguments) {
		throw new RuntimeException(format(template, arguments));
	}
}
