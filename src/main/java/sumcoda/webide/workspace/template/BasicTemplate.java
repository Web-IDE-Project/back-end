package sumcoda.webide.workspace.template;

import lombok.experimental.UtilityClass;

@UtilityClass
//언어별 기본 템플릿 클래스
public class BasicTemplate {
    private static final String C_TEMPLATE = """
        #include <stdio.h>

        int main() {
            printf("%s\\n");
            return 0;
        }
        """;

    private static final String CPP_TEMPLATE = """
        #include <iostream>

        int main() {
            std::cout << "%s" << std::endl;
            return 0;
        }
        """;

    private static final String JAVA_TEMPLATE = """
        public class Main {
            public static void main(String[] args) {
                System.out.println("%s");
            }
        }
        """;

    private static final String JAVASCRIPT_TEMPLATE = """
        console.log('%s');
        """;

    private static final String PYTHON_TEMPLATE = """
        print('%s')
        """;

    public static String getTemplate(String language) {
        return switch (language) {
            case "C" -> C_TEMPLATE;
            case "CPP" -> CPP_TEMPLATE;
            case "JAVA" -> JAVA_TEMPLATE;
            case "JAVASCRIPT" -> JAVASCRIPT_TEMPLATE;
            case "PYTHON" -> PYTHON_TEMPLATE;
            default -> "";
        };
    }
}