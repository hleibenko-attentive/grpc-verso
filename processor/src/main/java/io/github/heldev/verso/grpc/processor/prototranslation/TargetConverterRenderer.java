package io.github.heldev.verso.grpc.processor.prototranslation;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import io.github.heldev.verso.grpc.processor.prototranslation.field.FieldSource;
import io.github.heldev.verso.grpc.processor.prototranslation.field.GetterFieldSource;
import io.github.heldev.verso.grpc.processor.prototranslation.field.TranslatorFieldSource;

import java.util.Map;

import static com.squareup.javapoet.MethodSpec.constructorBuilder;
import static com.squareup.javapoet.MethodSpec.methodBuilder;
import static com.squareup.javapoet.TypeSpec.classBuilder;
import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

public class TargetConverterRenderer {

	public JavaFile render(TargetTranslatorsViewModel translator) {
		return JavaFile.builder(translator.javaPackage(), renderClass(translator))
				.indent("\t")
				.build();
	}

	private TypeSpec renderClass(TargetTranslatorsViewModel translator) {
		return classBuilder(translator.name())
				.addModifiers(PUBLIC, ABSTRACT)
				.addMethod(constructorBuilder().addModifiers(PRIVATE).build())
				.addMethod(renderToBuilderMethod(translator))
				.build();
	}

	private MethodSpec renderToBuilderMethod(TargetTranslatorsViewModel translator) {
		TypeName builderType = TypeName.get(translator.targetBuilderType());

		return methodBuilder("toBuilder")
				.addModifiers(PUBLIC, STATIC)
				.returns(builderType)
				.addParameter(TypeName.get(translator.sourceType()), "message")
				.addCode("return $T.builder()\n\t\t", TypeName.get(translator.targetType()))
				.addStatement(renderBuilderCalls(translator))
				.build();
	}

	private CodeBlock renderBuilderCalls(TargetTranslatorsViewModel translator) {
		return translator.fieldSources()
				.entrySet().stream()
				.map(this::renderBuilderCall)
				.collect(CodeBlock.joining("\n"));
	}

	private CodeBlock renderBuilderCall(Map.Entry<String, FieldSource> fieldWithSource) {
		return CodeBlock.of(
				".$L($L)",
				fieldWithSource.getKey(),
				renderFieldSource(fieldWithSource.getValue()));
	}

	private CodeBlock renderFieldSource(FieldSource fieldSource) {
		if (fieldSource instanceof GetterFieldSource) {
			return CodeBlock.of("message.$L()", ((GetterFieldSource) fieldSource).name());
		} else if (fieldSource instanceof TranslatorFieldSource) {
			TranslatorFieldSource source = (TranslatorFieldSource) fieldSource;

			return CodeBlock.of("$T.$L($L)",
					TypeName.get(source.translator().location()),
					source.translator().method(),
					renderFieldSource(source.underlyingSource()));
		} else {
			throw new RuntimeException("Unknown field source, it's a bug:" + fieldSource);
		}
	}
}
