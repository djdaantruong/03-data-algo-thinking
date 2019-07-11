package com.zalopay;

import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.Duration;
import java.time.LocalDateTime;

@Test
public class SlaServiceTest {
    private SlaService slaService;

    @DataProvider(name = "slaData")
    public static Object[][] slaData() {
        return new Object[][]{
                {
                        LocalDateTime.of(2019, 4, 30, 10, 0),
                        LocalDateTime.of(2019, 4, 30, 11, 0),
                        Duration.ofSeconds(3600) // 1h
                },
                {
                        LocalDateTime.of(2019, 4, 30, 11, 30),
                        LocalDateTime.of(2019, 4, 30, 14, 0),
                        Duration.ofSeconds(3600) // 1h
                },
                {
                        LocalDateTime.of(2019, 6, 27, 10, 0, 0),
                        LocalDateTime.of(2019, 7, 1, 14, 1, 0),
                        Duration.ofSeconds(79260) //1321m
                },
                {
                        LocalDateTime.of(2019, 4, 30, 10, 0),
                        LocalDateTime.of(2019, 4, 30, 11, 0),
                        Duration.ofSeconds(3600) // 1h
                },
                {
                        LocalDateTime.of(2019, 4, 30, 11, 30),
                        LocalDateTime.of(2019, 4, 30, 14, 0),
                        Duration.ofSeconds(3600) // 1h
                }
                ,
                {
                        LocalDateTime.of(2019, 4, 30, 11, 30),
                        LocalDateTime.of(2019, 4, 30, 11, 45),
                        Duration.ofMinutes(15)
                },
                {
                        LocalDateTime.of(2019, 4, 30, 15, 30),
                        LocalDateTime.of(2019, 4, 30, 17, 45),
                        Duration.ofMinutes(135)
                },
                {
                        LocalDateTime.of(2019, 4, 30, 15, 00),
                        LocalDateTime.of(2019, 5, 1, 9, 00),
                        Duration.ofMinutes(210) //3h + 30
                },
                {
                        LocalDateTime.of(2019, 4, 30, 15, 00),
                        LocalDateTime.of(2019, 5, 3, 9, 00),
                        Duration.ofMinutes(210 + 480 * 2)
                },
                {
                        LocalDateTime.of(2019, 4, 30, 15, 00),
                        LocalDateTime.of(2019, 5, 10, 9, 00),
                        Duration.ofMinutes(210 + 480 * 9 - 270 - 480)
                }
                ,
                {
                        LocalDateTime.of(2019, 4, 30, 15, 00),
                        LocalDateTime.of(2019, 5, 13, 9, 00),
                        Duration.ofMinutes(210 + 480 * 12 - (270 + 480) * 2)
                }
                ,
                {
                        LocalDateTime.of(2019, 4, 30, 9, 00),
                        LocalDateTime.of(2019, 5, 13, 15, 00),
                        Duration.ofMinutes(750 + 480 * 12 - (270 + 480) * 2)
                },
                {
                        LocalDateTime.of(2019, 4, 30, 9, 00),
                        LocalDateTime.of(2019, 5, 13, 9, 00),
                        Duration.ofMinutes(480 + 480 * 12 - (270 + 480) * 2)
                }
        };
    }


    @BeforeClass
    public void init() {
        slaService = new SlaServiceImpl();
    }

    // GIVEN
    @Test(dataProvider = "slaData")
    public void calculate(LocalDateTime start, LocalDateTime end, Duration expectedResult) {
        // WHEN
        Duration duration = slaService.calculate(start, end);

        // THEN
        MatcherAssert.assertThat(duration, Is.is(expectedResult));
    }
}