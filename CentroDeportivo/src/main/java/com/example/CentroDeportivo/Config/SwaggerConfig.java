package com.example.CentroDeportivo.Config;



import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API ActivaU - Centro Deportivo")
                        .version("1.0")
                        .description("Documentación de la API para gestionar reservas, clases, membresías y pagos del centro deportivo ActivaU")
                        .contact(new Contact()
                                .email("tu_correo@ucundinamarca.edu.co")));
    }
}