package habuma.songsapi;

import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.ai.parser.BeanOutputParser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/songs")
public class SongsController {

    private final OpenAiChatClient aiClient;

    public SongsController(OpenAiChatClient aiClient) {
        this.aiClient = aiClient;
    }

    @GetMapping("/top-song")
    public String getTopSong() {
        String prompt = "What was the Billboard number one year-end top 100 single for 1980?";
        return aiClient.call(prompt);
    }

//    @GetMapping("/top-song/{year}")
//    public String getTopSong(@PathVariable("year") int year) {
//        String prompt = "What was the Billboard number one year-end top 100 single for {year}?";
//        PromptTemplate template = new PromptTemplate(prompt);
//        template.add("year", year);
//        return aiClient.call(template.render());
//    }

    @GetMapping("/top-song/{year}")
    public TopSong getTopSong(@PathVariable("year") int year) {

        BeanOutputParser<TopSong> parser = new BeanOutputParser<>(TopSong.class);
        String promptString = """
                What was the Billboard number one year-end top 100 single of {year}?
                {format}
                """;
        PromptTemplate template = new PromptTemplate(promptString);
        template.add("year", year);
//        template.add("format", "Give me JSON output. here are the fields...");
        template.add("format", parser.getFormat());
        template.setOutputParser(parser);

        System.err.println(" FORMAT STRING: " + parser.getFormat());

        Prompt prompt = template.create();
        ChatResponse aiResponse = aiClient.call(prompt);
        String text = aiResponse.getResult().getOutput().getContent();

        return parser.parse(text);
    }
}
