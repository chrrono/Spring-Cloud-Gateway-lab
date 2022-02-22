package com.grapeup.reactive.marketing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.temporal.TemporalUnit;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@SpringBootApplication
public class MarketingApplication {

	public static void main(String[] args) {
		SpringApplication.run(MarketingApplication.class, args);
	}

	private final static Article [] ARTICLES = new Article[] {
			new Article("How to Simplify the Process of Building Production-ready AI Services",
					"https://grapeup.com/blog/how-to-simplify-the-process-of-building-production-ready-ai-services/"),
			new Article("Should UI Testing and API Testing Go Together?",
					"https://grapeup.com/blog/should-ui-testing-and-api-testing-go-together/"),
			new Article("Benefits of Using Immutable.js With React & Redux Apps",
					"https://grapeup.com/blog/benefits-of-using-immutable-js-with-react-redux-apps/"),
			new Article("Kafka Transactions – Integrating with Legacy Systems",
					"https://grapeup.com/blog/kafka-transactions-integrating-with-legacy-systems/")
	};

	private AtomicInteger incrementer = new AtomicInteger(0);

	private final Flux<Article> articlesStream = Flux.fromStream(
					Stream.generate(() -> ARTICLES[incrementer.getAndIncrement() % ARTICLES.length]))
			.delayElements(Duration.ofMillis(4000));

	@Bean
	Flux<Article> articles() {
		return this.articlesStream.publish().autoConnect();
	}

}

@RestController
class ReactiveArticlesController {

	@Autowired
	private Flux<Article> articles;

	@GetMapping(
			produces = MediaType.TEXT_EVENT_STREAM_VALUE,
			value = "/articles"
	)
	public Flux<Article> getArticles() {
		return this.articles;
	}

}


