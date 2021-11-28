package io.github.heldev.verso.grpc.processor;

import io.github.heldev.verso.grpc.processor.common.DefinitionLoader;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DefinitionLoaderTest {

	@Test
	void loads_definitions() {
		assertThat(new DefinitionLoader().loadDefinitions()).isNotNull();
	}
}
