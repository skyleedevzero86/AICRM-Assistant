package com.aicrm.core.category.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.aicrm.core.category.application.GetConsultationCategoryTreeService;
import com.aicrm.core.category.dto.ConsultationCategoryTreeResponse;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ConsultationCategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class ConsultationCategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GetConsultationCategoryTreeService getConsultationCategoryTreeService;

    @Test
    void getTreeReturnsNestedCategoryStructure() throws Exception {
        // given
        when(getConsultationCategoryTreeService.getActiveTree()).thenReturn(List.of(
                new ConsultationCategoryTreeResponse(
                        1L,
                        "DELIVERY",
                        "배송",
                        1,
                        List.of(
                                new ConsultationCategoryTreeResponse(
                                        2L,
                                        "DELIVERY_STATUS",
                                        "배송 상태",
                                        2,
                                        List.of(
                                                new ConsultationCategoryTreeResponse(
                                                        3L,
                                                        "DELIVERY_DELAY",
                                                        "배송 지연",
                                                        3,
                                                        List.of()
                                                )
                                        )
                                )
                        )
                )
        ));

        // when & then
        mockMvc.perform(get("/api/categories/consultation/tree"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].code").value("DELIVERY"))
                .andExpect(jsonPath("$.data[0].children[0].children[0].code").value("DELIVERY_DELAY"))
                .andExpect(jsonPath("$.data[0].children[0].children[0].depth").value(3));
    }
}
