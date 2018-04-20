package io.github.hexfaker.chad;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Vsevolod Poletaev (hexfaker)
 */
@Data
@EqualsAndHashCode(of = "id")
public class Question implements Comparable<Question> {

  private static Pattern RAW_QUESTION_PATTERN = Pattern.compile("(\\d+)\\.(.*)", Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE);


  public Question(int id, String question, String answer) {
    this.id = id;
    textWords = Stream.of(question.split(" ")).map(String::trim).collect(Collectors.toList());
    this.answer = answer;
  }

  @Override
  public int compareTo(Question o) {
    return this.id - o.id;
  }

  private final int id;
  private final List<String> textWords;
  private String answer;

  public String getText() {
    return String.join(" ", textWords);
  }

  public String formatWithAnswer() {
    return String.format("%s - %s",getText(), answer);
  }
}

