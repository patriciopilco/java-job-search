package com.xpoch.jobsearch;

import com.beust.jcommander.JCommander;
import com.xpoch.jobsearch.api.APIJobs;
import com.xpoch.jobsearch.cli.CLIArguments;
import com.xpoch.jobsearch.cli.CLIFunctions;

import java.util.*;
import java.util.stream.Stream;

import static com.xpoch.jobsearch.CommanderFunctions.buildCommanderWithName;
import static com.xpoch.jobsearch.CommanderFunctions.parseArguments;
import static com.xpoch.jobsearch.api.APIFunctions.buildAPI;

public class JobSearch {
    public static void main(String[] args) {
        JCommander jCommander = buildCommanderWithName("job-search", CLIArguments::newInstance);

        Stream<CLIArguments> streamOfCLI = parseArguments(jCommander, args, JCommander::usage)
                .orElse(Collections.emptyList())
                .stream()
                .map(obj -> (CLIArguments) obj);

        Optional<CLIArguments> cliArgumentsOptional =
                streamOfCLI.filter(cli -> !cli.isHelp())
                        .filter(cli -> cli.getKeyword() != null)
                        .findFirst();

        cliArgumentsOptional.map(CLIFunctions::toMap)
                .map(JobSearch::executeRequest)
                .orElse(Stream.empty())
                .forEach(System.out::println);
    }

    private static Stream<JobPosition> executeRequest(Map<String, Object> params) {
        APIJobs api = buildAPI(APIJobs.class, "https://jobs.github.com");

        return Stream.of(params)
                .map(api::jobs)
                .flatMap(Collection::stream);
    }
}
