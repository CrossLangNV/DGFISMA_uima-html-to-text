/*
 * Copyright 2017
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.crosslang.uimahtmltotext.uima;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.AnnotationBaseFS;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.CasUtil;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.CasCopier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.crosslang.sdk.utils.commons.CasDumperReadable;
import com.crosslang.uima.type.ProtectedSequence;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.api.transform.alignment.AlignedString;
import de.tudarmstadt.ukp.dkpro.core.api.transform.alignment.ImmutableInterval;
import de.tudarmstadt.ukp.dkpro.core.api.transform.alignment.Interval;

/**
 * 
 * This class was copied (with permission) from DKPro to adapt to our needs:
 * 
 * <ul>
 * <li>Make it a JCasAnnotator instead of a JCasMultiplier
 * <li>Refactor the code for readability
 * </ul>
 * 
 * Base-class for normalizers that do insert/delete/replace operations. Please
 * mind that these operations must not overlap!.
 */
public abstract class JCasTransformer_ImplBase extends JCasAnnotator_ImplBase {

	private static final Logger log = LoggerFactory.getLogger(JCasTransformer_ImplBase.class);

	private JCas input;
	private List<Change> changes;

	/**
	 * Define the target view to work on.
	 */
	public static final String PARAM_TARGET_VIEW_NAME = "targetViewName";
	@ConfigurationParameter(name = PARAM_TARGET_VIEW_NAME, mandatory = true)
	private String targetViewName;

	/**
	 * A list of fully qualified type names that should be copied to the transformed
	 * CAS where available. By default, no types are copied apart from
	 * {@link DocumentMetaData}, i.e. all other annotations are omitted.
	 */
	public static final String PARAM_TYPES_TO_COPY = "typesToCopy";
	@ConfigurationParameter(name = PARAM_TYPES_TO_COPY, mandatory = true, defaultValue = {})
	protected String[] typesToCopy;

	public static final String PARAM_REMOVE_OVERLAPPING = "removeOverlapping";
	@ConfigurationParameter(name = PARAM_REMOVE_OVERLAPPING, mandatory = false, defaultValue = "true")
	private boolean removeOverlapping = true;

	/**
	 * 
	 * Initialization before processing
	 * 
	 * @param inputCas
	 * @param outputCas
	 * @throws AnalysisEngineProcessException
	 */
	public void beforeProcess(JCas inputCas, JCas outputCas) throws AnalysisEngineProcessException {

		// If the source document has a language set, copy it to the target
		if (inputCas.getDocumentLanguage() != null) {
			outputCas.setDocumentLanguage(inputCas.getDocumentLanguage());
		}

		// Captures the list of changes (insert / delete / replace)
		changes = new ArrayList<Change>();

		// Remember the input CAS so that we can access its text in replace()
		input = inputCas;
	}

	/**
	 * 
	 * 
	 * 
	 * @param inputCat
	 * @param aOutput
	 */
	public void afterProcess(JCas inputCas, JCas outputCas) {

		log.info("Start afterProcess");
		// Track changes in the alignedString
		AlignedString alignedString = new AlignedString(inputCas.getDocumentText());

		log.info("Checkoverlap...");
		// Check overlapping changes, because these would cause corrupt output
		Collections.sort(changes, Interval.SEG_START_CMP);
		checkOverlap(inputCas);
		log.info("Checkoverlap done");

		log.info("processChanges...");
		// Apply the changes to our alignedString
		processChanges(alignedString, outputCas);
		log.info("processChanges done");

		log.info("get alignedString...");
		// Set modified string on output CAS
		outputCas.setDocumentText(alignedString.get());
		log.info("alignedString done");

		// Copy the annotation types mentioned in PARAM_TYPES_TO_COPY
		log.info("copy annotations...");
		CasCopier copier = new CasCopier(inputCas.getCas(), outputCas.getCas());
		for (String typeName : typesToCopy) {
			handleType(inputCas, outputCas, alignedString, copier, typeName);
		}
		log.info("copy annotations done");
	}

	private void checkOverlap(JCas cas) {
		List<Change> toRemove = new ArrayList<>();
		for (int i = 0; i < changes.size(); i++) {
			Change change1 = changes.get(i);
			for (int j = i; j < changes.size(); j++) {
				if (i != j) {
					Change change2 = changes.get(j);
					if (change1.overlaps(change2)) {
						if (log.isTraceEnabled()) {
							CasDumperReadable.dump(cas);
							log.trace("Change {} must not overlap with {}", change1, change2);
						}
						if (removeOverlapping) {
							toRemove.add(change2);
						}
					}
				}
			}

		}
		changes.removeAll(toRemove);
	}

	private void processChanges(AlignedString alignedString, JCas jcas) {
		// Apply changes in reverse order so that offsets of unprocessed changes remain
		// valid
		log.info("got {} changes", changes.size());
		ListIterator<Change> li = changes.listIterator(changes.size());
		while (li.hasPrevious()) {
			Change change = li.previous();
			switch (change.getAction()) {
			case INSERT:
				alignedString.insert(change.getStart(), change.getText());
				break;
			case DELETE:
				alignedString.delete(change.getStart(), change.getEnd());
				break;
			case REPLACE:
				alignedString.replace(change.getStart(), change.getEnd(), change.getText());
				break;
			default:
				throw new IllegalStateException("Unknown change action [" + change.getAction() + "]");
			}
		}

		// Apply all the changes
		alignedString.get();

		// Write ProtectedSequence annotations
		ListIterator<Change> lili = changes.listIterator();
		while (lili.hasNext()) {
			Change change = lili.next();
			switch (change.getAction()) {
			case REPLACE:
				Interval i = alignedString.inverseResolve(new ImmutableInterval(change.getStart(), change.getEnd()));
				ProtectedSequence ps = new ProtectedSequence(jcas, i.getStart(), i.getEnd());
				ps.setId(change.getId());
				ps.setSource(change.getOldText());
				ps.setTarget(change.getOldText());
				ps.setName(change.getName());
				ps.addToIndexes();
				break;
			default:
				break;

			}
		}

	}

	private void handleType(JCas inputJCas, JCas outputJCas, AlignedString alignedString, CasCopier copier,
			String typeName) {

		CAS outputCas = outputJCas.getCas();
		CAS inputCas = inputJCas.getCas();
		Feature mDestSofaFeature = outputJCas.getTypeSystem().getFeatureByFullName(CAS.FEATURE_FULL_NAME_SOFA);
		Type annotationType = CasUtil.getType(outputCas, CAS.TYPE_NAME_ANNOTATION);
		Feature beginFeature = annotationType.getFeatureByBaseName(CAS.FEATURE_BASE_NAME_BEGIN);
		Feature endFeature = annotationType.getFeatureByBaseName(CAS.FEATURE_BASE_NAME_END);

		int c = 0;
		for (FeatureStructure fs : CasUtil.selectFS(inputCas, CasUtil.getType(inputCas, typeName))) {
			c++;
		}
		log.info("Found {} annotations", c);
		c = 0;
		for (FeatureStructure fs : CasUtil.selectFS(inputCas, CasUtil.getType(inputCas, typeName))) {
			log.info("Updating annotation ({})", c++);
			if (!copier.alreadyCopied(fs)) {
				FeatureStructure fsCopy = copier.copyFs(fs);
				// Make sure that the sofa annotation in the copy is set
				if (fs instanceof AnnotationBaseFS) {
					FeatureStructure sofa = fsCopy.getFeatureValue(mDestSofaFeature);
					if (sofa == null) {
						fsCopy.setFeatureValue(mDestSofaFeature, outputJCas.getSofa());
					}
				}

				// Update the begin/end offsets
				if (fs instanceof AnnotationFS) {
					AnnotationFS annoFs = (AnnotationFS) fs;
					long start = System.nanoTime();
					Interval i = alignedString
							.inverseResolve(new ImmutableInterval(annoFs.getBegin(), annoFs.getEnd()));
					long elapsedTime = System.nanoTime() - start;
					log.info("resolve took: {}ms", elapsedTime / 1000000);
					fsCopy.setIntValue(beginFeature, i.getStart());
					fsCopy.setIntValue(endFeature, i.getEnd());
				}

				outputJCas.addFsToIndexes(fsCopy);
			}
		}
	}

	public void insert(int aBegin, String aText) {
		changes.add(new Change(ChangeAction.INSERT, aBegin, aBegin, aText, null, null, -1));
	}

	public void delete(int aBegin, int aEnd) {
		changes.add(new Change(ChangeAction.DELETE, aBegin, aEnd, null, null, null, -1));
	}

	public void replace(int aBegin, int aEnd, String aText, String aOldText, String aName, int id) {
		// Create a change action only if the new text differs from the old text.
		// This avoids clutter in the changes list and improves performance when
		// applying the changes later.
		if (!aText.equals(input.getDocumentText().substring(aBegin, aEnd))) {
			changes.add(new Change(ChangeAction.REPLACE, aBegin, aEnd, aText, aOldText, aName, id));
		}
	}

	@Override
	public void process(JCas input) throws AnalysisEngineProcessException {

		JCas output = JCasUtil.getView(input, targetViewName, true);

		beforeProcess(input, output);
		process(input, output);
		afterProcess(input, output);

	}

	public abstract void process(JCas aInput, JCas aOutput) throws AnalysisEngineProcessException;

	// --

	private static enum ChangeAction {
		INSERT, DELETE, REPLACE
	}

	private static class Change extends ImmutableInterval {
		private ChangeAction action;
		private String text;
		private String oldText;
		private String name;
		private int id;

		public Change(ChangeAction aAction, int aBegin, int aEnd, String aText, String aOldText, String aName,
				int aId) {
			super(aBegin, aEnd);
			action = aAction;
			text = aText;
			oldText = aOldText;
			name = aName;
			id = aId;
		}

		public ChangeAction getAction() {
			return action;
		}

		public String getText() {
			return text;
		}

		public String getName() {
			return name;
		}

		public String getOldText() {
			return oldText;
		}

		public int getId() {
			return id;
		}
	}
}
