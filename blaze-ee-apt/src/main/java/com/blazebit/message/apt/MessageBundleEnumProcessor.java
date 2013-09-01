/*
 * Copyright 2013 Blazebit.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blazebit.message.apt;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.Writer;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;

import org.apache.deltaspike.core.api.message.MessageBundle;

import com.blazebit.i18n.LocaleUtils;

/**
 * 
 * @author Christian
 */
@SupportedAnnotationTypes("org.apache.deltaspike.core.api.message.annotation.MessageBundle")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public abstract class MessageBundleEnumProcessor extends AbstractProcessor {
    
    private static final Comparator<Locale> LOCALE_COMPARATOR = new Comparator<Locale>() {

        @Override
        public int compare(Locale o1, Locale o2) {
            return o1.toString().compareTo(o2.toString());
        }
        
    };

	@Override
	public boolean process(Set<? extends TypeElement> annotations,
			RoundEnvironment roundEnv) {
		if (annotations.isEmpty()) {
			return true;
		}

		try {
			Filer filer = processingEnv.getFiler();
			Map<String, MessageBundleInfo> messageBundles = new HashMap<String, MessageBundleInfo>();

			for (Element e : roundEnv
					.getElementsAnnotatedWith(MessageBundle.class)) {
				if (e instanceof TypeElement) {
					TypeElement messageBundleElement = (TypeElement) e;
					String messageBundle = messageBundleElement
							.getQualifiedName().toString();
					String messageBundleSimpleName = messageBundleElement.getSimpleName().toString();
					String messageBundleEnumName = messageBundle + "Enum";
					Set<String> messages = new HashSet<String>();

					for (Element messageBundleChild : messageBundleElement
							.getEnclosedElements()) {
						if (messageBundleChild instanceof ExecutableElement) {
							messages.add(getKey((ExecutableElement) messageBundleChild));
						}
					}

					if (!messages.isEmpty()) {
						String baseName = messageBundle.replaceAll("\\.", "/");
						FileObject javaFileObject;
						
						try {
							javaFileObject = filer.getResource(
								StandardLocation.SOURCE_PATH, "", baseName
										+ ".java");
						} catch(FileNotFoundException ex) {
							throw new IllegalArgumentException("Could not find the source file '" + baseName + ".java' that actually triggered the enum generation process", ex);
						}
						
						long lastModified = javaFileObject.getLastModified();
						Set<Locale> locales = new TreeSet<Locale>(LOCALE_COMPARATOR);
						URI baseUri = null;

						// Here we assume that the resources are present in the
						// source output location

						try {
							// Normally there is an english properties file
							try {
								baseUri = filer.getResource(
										StandardLocation.CLASS_PATH, "",
										baseName + "_en.properties").toUri();
							} catch (FileNotFoundException ex1) {
								try {
									baseUri = filer.getResource(
											StandardLocation.CLASS_OUTPUT, "",
											baseName + "_en.properties").toUri();
								} catch (FileNotFoundException ex2) {
									baseUri = filer.getResource(
											StandardLocation.SOURCE_PATH, "",
											baseName + "_en.properties").toUri();
								}
							}
						} catch (FileNotFoundException ex1) {
							// Otherwise we have to go through all available
							// locales
							for (Locale l : Locale.getAvailableLocales()) {
								String path;

								try {
									path = baseName + "_" + l.getLanguage()
											+ ".properties";
									baseUri = filer.getResource(
											StandardLocation.CLASS_PATH, "",
											path).toUri();
									break;
								} catch (FileNotFoundException ex2) {
									try {
										path = baseName + "_" + l.getLanguage()
												+ ".properties";
										baseUri = filer.getResource(
												StandardLocation.CLASS_OUTPUT, "",
												path).toUri();
										break;
									} catch (FileNotFoundException ex3) {
										try {
											path = baseName + "_" + l.getLanguage()
													+ ".properties";
											baseUri = filer.getResource(
													StandardLocation.SOURCE_PATH, "",
													path).toUri();
											break;
										} catch (FileNotFoundException ex4) {
											// Nothing we can do about it
										}
									}
								}

								if (l.getCountry() != null
										&& !l.getCountry().isEmpty()) {


									try {
										path = baseName + "_" + l.getLanguage()
												+ "_" + l.getCountry()
												+ ".properties";
										baseUri = filer.getResource(
												StandardLocation.CLASS_PATH,
												"", path).toUri();
										break;
									} catch (FileNotFoundException ex2) {
										try {
											path = baseName + "_" + l.getLanguage()
													+ "_" + l.getCountry()
													+ ".properties";
											baseUri = filer.getResource(
													StandardLocation.CLASS_OUTPUT,
													"", path).toUri();
											break;
										} catch (FileNotFoundException ex3) {
											try {
												path = baseName + "_" + l.getLanguage()
														+ "_" + l.getCountry()
														+ ".properties";
												baseUri = filer.getResource(
														StandardLocation.SOURCE_PATH,
														"", path).toUri();
												break;
											} catch (FileNotFoundException ex4) {
												// Nothing we can do about it
											}
										}
									}
								}
							}
						}

						if (baseUri == null) {
							throw new IllegalArgumentException(
									"No properties files could be found for '"
											+ messageBundle + "'");
						}

						File baseDir = new File(baseUri).getParentFile();
						
						if(!baseDir.exists() || !baseDir.isDirectory()) {
							throw new IllegalArgumentException("The base directory for the properties files could not be found!");
						}

						for (File propertiesFile : baseDir.listFiles()) {
						    // Only check properties files that belong to the message bundle
						    if(propertiesFile.getName().startsWith(messageBundleSimpleName + "_") || propertiesFile.getName().equals(messageBundleSimpleName + ".properties")) {
    							checkPropertiesFile(propertiesFile, messages);
    							String name = propertiesFile.getName();
    							int index = name.indexOf('_');
    							int dotIndex = name.lastIndexOf('.');
    
    							if (index > -1 && dotIndex > -1) {
    								locales.add(LocaleUtils.getLocale(name
    										.substring(index + 1, dotIndex)));
    							}
						    }
						}

						MessageBundleInfo info = new MessageBundleInfo(
								baseName, lastModified, locales, messages);
						messageBundles.put(messageBundleEnumName, info);
					}
				}
			}

			for (Map.Entry<String, MessageBundleInfo> entry : messageBundles
					.entrySet()) {
				String className = entry.getKey();
				JavaFileObject jfo = filer.createSourceFile(className);

				if (jfo.getLastModified() == entry.getValue().getLastModified()) {
					// Skip unchanged files
					continue;
				}

				Writer writer = null;

				try {
					writer = jfo.openWriter();
					generateClass(className, entry.getValue(), writer);
				} finally {
					if (writer != null) {
						writer.close();
					}
				}

				File generatedFile = new File(jfo.toUri());
				generatedFile.setLastModified(entry.getValue()
						.getLastModified());
			}
		} catch (Exception ex) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ex.printStackTrace(new PrintStream(baos));
			String message = baos.toString();
			processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
					"Could not generate enums\n" + message);
			return false;
		}

		return true;
	}

	protected void checkPropertiesFile(File propertiesFile,
			Collection<String> messages) {
		// Check if the messages from the list all exist in the file
		InputStream is = null;

		try {
			Properties props = new Properties();
			is = new FileInputStream(propertiesFile);
			props.load(is);

			for (String message : messages) {
				if (!props.containsKey(message)) {
					processingEnv.getMessager().printMessage(
							Kind.ERROR,
							"Properties file '" + propertiesFile.getName()
									+ "' is missing the property '" + message
									+ "'");
				}
			}
		} catch (Exception ex) {
			throw new RuntimeException("Could not check properties file '"
					+ propertiesFile.getName() + "'", ex);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					// Ignore
				}
			}
		}
	}

	protected String getKey(ExecutableElement messageElement) {
		return messageElement.getSimpleName().toString();

		// Alternative implementation that converts camel case names to upper
		// case enum like names

		// String key = messageElement.getSimpleName().toString();
		// StringBuilder enumKeySb = new StringBuilder();
		// char[] keyChars = key.toCharArray();
		//
		// enumKeySb.append(Character.toUpperCase(keyChars[0]));
		//
		// for (int i = 1; i < keyChars.length; i++) {
		// if (Character.isUpperCase(keyChars[i])) {
		// enumKeySb.append('_').append(keyChars[i]);
		// } else {
		// enumKeySb.append(Character.toUpperCase(keyChars[i]));
		// }
		// }
		//
		// return enumKeySb.toString();
	}

	protected void generateClass(String className, MessageBundleInfo info,
			Writer writer) throws Exception {
		String packageName = className.substring(0, className.lastIndexOf('.'));
		String simpleName = className.substring(packageName.length() + 1);
		Collection<String> imports = getImports(className);
		Collection<String> keys = info.getMessages();

		writer.append("package ").append(packageName).append(";\n\n");

		if (!imports.isEmpty()) {
			for (String importElement : imports) {
				writer.append("import ").append(importElement).append(";\n");
			}

			writer.append("\n");
		}

		writer.append("public enum ").append(simpleName).append(" {\n\n");

		if (!keys.isEmpty()) {
		    Iterator<String> keysIter = keys.iterator();
			writer.append("\t").append(keysIter.next());

			while (keysIter.hasNext()) {
				writer.append(",\n\t").append(keysIter.next());
			}

			writer.append(";\n\n");
		}

		generateBody(className, writer);

		writer.append("}\n");
	}

	protected Collection<String> getImports(String enumClassName) {
		return Collections.emptyList();
	}

	protected void generateBody(String enumClassName, Writer writer) {
		// Default Noop
	}
}
