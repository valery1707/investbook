/*
 * InvestBook
 * Copyright (C) 2021  Vitalii Ananev <spacious-team@ya.ru>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ru.investbook.service.moex;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class MoexDerivativeCodeServiceTest {

    @Mock
    RestTemplate restTemplate;

    @InjectMocks
    MoexDerivativeCodeService service;

    static Object[][] getFuturesCodes() {
        return new Object[][] {
                {"Si-6.21", "SiM1"},
                {"RTS-12.19", "RIZ9"},
                {"SiM1", "SiM1"},
                {"RIZ9", "RIZ9"},
                {"BR-5.21", "BRK1"},
                {"BRK1", "BRK1"},
                {"Si65000BC9D", null},
                {"abc", null},
                {"Si-6.211", null},
                {"SI-6.211", null},
                {"Si-13.21", null},
                {"Si-0.21", null}
        };
    }

    @ParameterizedTest
    @MethodSource("getFuturesCodes")
    void getFuturesCode(String shortName, String code) {
        if (shortName != null) {
            assertEquals(service.getFuturesCode(shortName).orElse(null), code);
        }
    }

    static Object[][] getFuturesShortnames() {
        return new Object[][] {
                {"SiM1", "Si-6.21"},
                {"RIZ9", "RTS-12.19"},
                {"SRX1", "SBRF-11.21"},
                {"SRV2", "SBRF-10.22"},
                {"SPQ9", "SBPR-8.19"},
                {"BRK3", "BR-5.23"},
                {"Si-6.21", "Si-6.21"},
                {"RTS-12.19", "RTS-12.19"},
                {"BR-5.21", "BR-5.21"},
                {"Si65000BC9D", null},
                {"abc", null},
                {"Si-6.211", null},
                {"SI-6.211", null},
                {"Si-13.21", null},
                {"Si-0.21", null},
                {"BRE1", null}
        };
    }

    @ParameterizedTest
    @MethodSource("getFuturesShortnames")
    void getFuturesShortname(String code, String shortName) {
        assertEquals(service.getFuturesShortname(code).orElse(null), shortName);
    }

    @ParameterizedTest
    @MethodSource("getFuturesCodes")
    void isFuturesTest1(String shortName, String code) {
        assertEquals(service.isFutures(shortName), code != null);
    }

    @ParameterizedTest
    @MethodSource("getFuturesShortnames")
    void isFuturesTest2(String code, String shortName) {
        assertEquals(service.isFutures(code), shortName != null);
    }

    static Object[][] getOptionShortnames() {
        return new Object[][] {
                {"BR-7.16M270616CA 50", "BR-7.16M270616CA 50"},
                {"BR50BF6", "BR-7.16M270616CA 50"},
                {"BR50BE1", "BR-6.21M250521CA50"},
                {"BR50BE1A", "BR-6.21M060521CA50"},
                {"Ri150000BS9B", null},
                {"RI150000B9", null},
                {"RI150000S9B", null},
                {"RIBS9B", null},
                {"RI1a5BS9", null},
                {"abc", null},
                {"SiZ1", null},
                {"Si-6.21", null}
        };
    }

//    @ParameterizedTest
//    @MethodSource("getOptionShortnames")
//    void getOptionShortnamesTest(String contract, String shortname) {
//        assertEquals(moexIssClient.getOptionShortname(contract).orElse(null), shortname);
//    }

    static Object[][] getOptionUnderlingFutures() {
        return new Object[][] {
                {"Si75000BL1", "SiZ1"},
                {"Si75000BL0D", "SiH1"},
                {"RI150000BG9", "RIU9"},
                {"RI150000BS9", "RIU9"},
                {"RI150000BG9A", "RIU9"},
                {"RI150000BS9B", "RIU9"},
                {"RI90000BS9", "RIU9"},
                {"BR50BE1", "BRM1"},
                {"BR50BE1A", "BRM1"},
                {"BR50BF1", "BRN1"},
                //{"BR-10BE0", "BRM0"}, // пока только пример из документации МосБиржи
                {"Ri150000BS9B", null},
                {"RI150000B9", null},
                {"RI150000S9B", null},
                {"RIBS9B", null},
                {"RI1a5BS9", null},
                {"abc", null},
                {"SiZ1", null},
                {"Si-6.21", null}
        };
    }

//    Requires internet connection
//    @ParameterizedTest
//    @MethodSource("getOptionUnderlingFutures")
//    void getOptionUnderlingFuturesTest(String optionCode, String futuresCode) {
//        assertEquals(moexIssClient.getOptionUnderlingFutures(optionCode).orElse(null), futuresCode);
//    }

    @ParameterizedTest
    @MethodSource("getOptionShortnames")
    void isOptionTest1(String shortName, String code) {
        assertEquals(service.isOption(shortName), code != null);
    }

    @ParameterizedTest
    @MethodSource("getOptionUnderlingFutures")
    void isOptionTest2(String code, String shortName) {
        assertEquals(service.isOption(code), shortName != null);
    }
}