package sumcoda.webide.common.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping;

@Configuration
@RequiredArgsConstructor
public class QueryDSLConfig {

    private final EntityManager entityManager;
    private final BeanNameUrlHandlerMapping beanNameHandlerMapping;

    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(entityManager);
    }

}
