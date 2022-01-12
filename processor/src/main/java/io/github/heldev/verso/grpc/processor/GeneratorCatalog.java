package io.github.heldev.verso.grpc.processor;

import org.immutables.value.Value;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import static io.github.heldev.verso.grpc.processor.common.Utils.panicV;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@Value.Immutable
public abstract class GeneratorCatalog {
	protected abstract Map<String, Generator> generatorsByName();

	public static GeneratorCatalog of(Collection<Generator> generators) {
		return ImmutableGeneratorCatalog.builder()
				.generatorsByName(indexByName(generators))
				.build();
	}

	private static Map<String, Generator> indexByName(Collection<Generator> generators) {
		return generators.stream()
				.collect(toMap(
						Generator::name, identity(),
						(name, ignored) -> panicV("generator names should be unique, non unique name" + name)));
	}

	public Optional<Generator> findByName(String name) {
		return Optional.ofNullable(generatorsByName().get(name));
	}
}
