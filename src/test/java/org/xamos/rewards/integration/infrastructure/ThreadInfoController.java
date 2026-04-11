package org.xamos.rewards.integration.infrastructure;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Internal controller used only for infrastructure verification and smoke testing.
 */
@RestController
public class ThreadInfoController {

    @GetMapping("/internal/thread-info")
    public String getThreadInfo() {
        return Thread.currentThread().toString();
    }
}
