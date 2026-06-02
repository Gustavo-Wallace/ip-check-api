package br.com.gustavo.ip_check_api.utils;

import br.com.gustavo.ip_check_api.dtos.IpAddressImportItemDTO;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IpAddressCsvParserTest {

    @Test
    void shouldParseCsvWithHeaderAndDescriptions() {
        String csvContent = """
                address,description
                8.8.8.8,Google DNS
                1.1.1.1,Cloudflare DNS
                """;

        List<IpAddressImportItemDTO> result = IpAddressCsvParser.parse(csvContent);

        assertEquals(2, result.size());

        assertEquals("8.8.8.8", result.get(0).getAddress());
        assertEquals("Google DNS", result.get(0).getDescription());

        assertEquals("1.1.1.1", result.get(1).getAddress());
        assertEquals("Cloudflare DNS", result.get(1).getDescription());
    }

    @Test
    void shouldUseDefaultDescriptionWhenDescriptionColumnIsEmpty() {
        String csvContent = """
                address,description
                8.8.8.8,
                """;

        List<IpAddressImportItemDTO> result = IpAddressCsvParser.parse(csvContent);

        assertEquals(1, result.size());
        assertEquals("8.8.8.8", result.get(0).getAddress());
        assertEquals("Imported from CSV text", result.get(0).getDescription());
    }

    @Test
    void shouldIgnoreBlankLines() {
        String csvContent = """
                address,description

                8.8.8.8,Google DNS

                1.1.1.1,Cloudflare DNS
                """;

        List<IpAddressImportItemDTO> result = IpAddressCsvParser.parse(csvContent);

        assertEquals(2, result.size());
    }

    @Test
    void shouldIgnoreRowsWithBlankAddress() {
        String csvContent = """
                address,description
                ,Missing IP
                8.8.8.8,Google DNS
                """;

        List<IpAddressImportItemDTO> result = IpAddressCsvParser.parse(csvContent);

        assertEquals(1, result.size());
        assertEquals("8.8.8.8", result.get(0).getAddress());
    }

    @Test
    void shouldReturnEmptyListWhenCsvContentIsBlank() {
        List<IpAddressImportItemDTO> result = IpAddressCsvParser.parse(" ");

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptyListWhenCsvContentIsNull() {
        List<IpAddressImportItemDTO> result = IpAddressCsvParser.parse(null);

        assertTrue(result.isEmpty());
    }
}