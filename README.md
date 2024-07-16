hsql spring boot
https://www.thetechnojournals.com/2019/10/how-to-read-large-file-in-java.html
public Map<String, Integer> asyncYearCount(String file) throws IOException, InterruptedException, ExecutionException {
        try {
            List<CompletableFuture<Map<String, Integer>>> futures = new ArrayList<>();
            
            List<String> items = new ArrayList<>();
            Files.lines(Paths.get(file))
            .skip(1)//skip first line
            .forEach(line->{
                items.add(line);
                if(items.size()%10000==0) {
                    //add completable task for each of 10000 rows
                    futures.add(CompletableFuture.supplyAsync(yearCountSupplier(new ArrayList<>(items), new HashMap<>())));
                    items.clear();
                }
            });
            if(items.size()>0) {
                //add completable task for remaining rows
                futures.add(CompletableFuture.supplyAsync(yearCountSupplier(items, new HashMap<>())));
            }
            return CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]))
                .thenApply($->{
                    //join all task to collect result after all tasks completed
                    return futures.stream().map(ftr->ftr.join()).collect(Collectors.toList());
                })
                .thenApply(maps->{
                    Map<String, Integer> yearCountMap = new HashMap<>();
                    maps.forEach(map->{
                        //merge the result of all the tasks
                        map.forEach((key, val)->{
                            if(yearCountMap.containsKey(key)) {
                                yearCountMap.put(key, yearCountMap.get(key)+val);
                            }else
                                yearCountMap.put(key, val);
                        });
                    });
                    return yearCountMap;
                })
                .get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }
