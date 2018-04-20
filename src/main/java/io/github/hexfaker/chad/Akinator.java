package io.github.hexfaker.chad;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Vsevolod Poletaev (hexfaker)
 */
@Slf4j
@Component
public class Akinator {

  private final static Pattern WORD_SANIIZER = Pattern.compile("[^\\w]", Pattern.UNICODE_CHARACTER_CLASS);

  private List<Question> questions;
  private HashMap<String, TreeSet<Question>> questionsByWords;

  public Akinator(QuestionLoader loader) {
    questions = loader.parse();
    buildDict();
  }

  private void buildDict() {
    questionsByWords = new HashMap<>();
    questions.forEach(
      q -> q.getTextWords().forEach(
        word -> questionsByWords.computeIfAbsent(sanitizeWord(word), k -> new TreeSet<>()).add(q)
      )
    );
  }

  private String sanitizeWord(String rawWord) {
    return WORD_SANIIZER.matcher(rawWord.toLowerCase()).replaceAll("");
  }

  String getAnswer(String keyWords) {
    List<TreeSet<Question>> possibleQuestionsSets = Stream.of(keyWords.split(" "))
      .map(this::sanitizeWord)
      .filter(Strings::isNotBlank)
      .map(questionsByWords::get)
      .filter(Objects::nonNull)
      .collect(Collectors.toList());

    if (possibleQuestionsSets.isEmpty())
      return "Сорян, кажется таких слов не было в вопросах. Только не бей(";

    TreeSet<Question> filteredQuestions = new TreeSet<>(possibleQuestionsSets.get(0));
    possibleQuestionsSets.remove(0);

    for (Set<Question> questions : possibleQuestionsSets)
      filteredQuestions.retainAll(questions);


    if (filteredQuestions.isEmpty())
      return "Блин, не нашел вопросов, содержащих все слова";

    return filteredQuestions.stream().map(Question::formatWithAnswer).collect( Collectors.joining("\n\n"));
  }
}
