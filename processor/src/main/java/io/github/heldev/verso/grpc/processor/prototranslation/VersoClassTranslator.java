package io.github.heldev.verso.grpc.processor.prototranslation;

import io.github.heldev.verso.grpc.interfaces.VersoClass;

import javax.lang.model.element.TypeElement;
import java.util.EnumSet;

import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.ElementKind.INTERFACE;

public class VersoClassTranslator {

	public TargetType translate(TypeElement element) {
		if (EnumSet.of(CLASS, INTERFACE).contains(element.getKind())) {
			return TargetType.builder()
					.protoMessage(element.getAnnotation(VersoClass.class).value())
					.type(element.asType())
					.build();

		} else {
			throw new RuntimeException("only classes and interfaces are supported");
		}
	}
}
