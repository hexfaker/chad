package io.github.hexfaker.chad;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Vsevolod Poletaev (hexfaker)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class QuestionLoader {
  private static String QUESTION_FILE_PATH = "classpath:questions.txt";
  private static String ANSWERS_FILE_PATH = "classpath:answers.csv";

  private static Pattern SINGLE_QUESTION_MATCHER = Pattern.compile("(\\d+)\\.(.*)((?:.|\\n)+?)\\n\\n",
    Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE);

  private final ResourceLoader loader;
  private Map<Integer, Integer> questionAnswersNo = new TreeMap<>();

  private void loadAnswers() {
    try {
      CSVParser p = new CSVParser(
        new InputStreamReader(loader.getResource(ANSWERS_FILE_PATH).getInputStream()),
        CSVFormat.EXCEL.withHeader()
      );

      p.getRecords().forEach(r -> {
        int id = Integer.parseInt(r.get(0));
        int answer = Integer.parseInt(r.get(1));

        questionAnswersNo.put(id, answer);
      });
    } catch (Exception e) {
      log.error("Failed to parse questions");
    }
  }

  public List<Question> parse() {
    loadAnswers();
    List<Question> res = new ArrayList<>();

    Resource resource = loader.getResource(QUESTION_FILE_PATH);
    try {
      Scanner scanner = new Scanner(resource.getInputStream());

      while (true) {
        String singleQuestion = scanner.findWithinHorizon(SINGLE_QUESTION_MATCHER, 0);

        if (singleQuestion == null)
          break;

        Matcher matcher = SINGLE_QUESTION_MATCHER.matcher(singleQuestion);

        if (matcher.matches()) {
          int id = Integer.parseInt(matcher.group(1));
          String question = matcher.group(2);
          String[] answers = matcher.group(3).split("\n");

         res.add(new Question(id, question, answers[questionAnswersNo.get(id)]));
        }
      }

    } catch (IOException e) {
      log.error("Failed to parse questions");
    }

    return res;
  }


}
