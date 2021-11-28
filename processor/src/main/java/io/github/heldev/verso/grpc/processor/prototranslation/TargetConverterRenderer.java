package io.github.heldev.verso.grpc.processor.prototranslation;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.Map;

import static com.squareup.javapoet.MethodSpec.constructorBuilder;
import static com.squareup.javapoet.MethodSpec.methodBuilder;
import static com.squareup.javapoet.TypeSpec.classBuilder;
import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

public class TargetConverterRenderer {

	public JavaFile render(TargetTranslatorViewModel translator) {
		return JavaFile.builder(translator.javaPackage(), renderClass(translator))
				.indent("\t")
				.build();
	}

	private TypeSpec renderClass(TargetTranslatorViewModel translator) {
		return classBuilder(translator.name())
				.addModifiers(PUBLIC, ABSTRACT)
				.addMethod(constructorBuilder().addModifiers(PRIVATE).build())
				.addMethod(renderToBuilderMethod(translator))
				.build();
	}

	private MethodSpec renderToBuilderMethod(TargetTranslatorViewModel translator) {
		TypeName builderType = TypeName.get(translator.targetBuilderType());

		return methodBuilder("toBuilder")
				.addModifiers(PUBLIC, STATIC)
				.returns(builderType)
				.addParameter(TypeName.get(translator.sourceType()), "message")
				.addCode("return $T.builder()\n\t\t", TypeName.get(translator.targetType()))
				.addStatement(renderBuilderCalls(translator))
				.build();
	}

	private CodeBlock renderBuilderCalls(TargetTranslatorViewModel translator) {
		return translator.fieldSources()
				.entrySet().stream()
				.map(this::renderBuilderCall)
				.collect(CodeBlock.joining("\n"));
	}

	private CodeBlock renderBuilderCall(Map.Entry<String, String> fieldWithSource) {
		return CodeBlock.of(
				".$L(message.$L())",
				fieldWithSource.getKey(),
				fieldWithSource.getValue());
	}
}
