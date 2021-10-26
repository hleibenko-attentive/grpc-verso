package io.github.heldev.verso.grpc.processor;

import io.github.heldev.verso.grpc.interfaces.VersoClass;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

public class VersoProcessor extends AbstractProcessor {

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		System.out.println("\n\n\n\t\t\tINITME\n\n\n");
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		System.out.println("\n\n\n\t\t\tTESTME\n\n\n");
		return false;
	}

	@Override
	public Set<String> getSupportedAnnotationTypes() {
		return Stream.of(VersoClass.class).map(Class::getCanonicalName).collect(toSet());
	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latest();
	}
}
