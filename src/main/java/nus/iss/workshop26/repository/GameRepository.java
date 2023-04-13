package nus.iss.workshop26.repository;

import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import nus.iss.workshop26.models.Game;

@Repository
public class GameRepository {

        @Autowired
        private MongoTemplate mongoTemplate;

        public List<Game> search(Integer limit, Integer offset) {
                // Pageable pageableRequest = PageRequest.of(offset, limit);
                // query.with(pageableRequest); (PageRequest.of first parameter skips the number of pages instead of number of entries, so it will return offset*limit instead of number of entries)
                Query query = Query.query(Criteria.where("gid").exists(true)).limit(limit).skip(offset);

                // lambda to find all the mongo game collections
                return mongoTemplate.find(query, Document.class, "games")
                                .stream()
                                .map(d -> Game.create(d))
                                .toList();
        }

        public List<Game> getGamesByRank() {
                Query query = new Query();
                query.with(Sort.by(Direction.ASC, "ranking"));
                // lambda to find all the mongo game collections sort by ranking
                return mongoTemplate.find(query, Document.class, "games")
                                .stream()
                                .map(d -> Game.create(d))
                                .toList();
        }

        public Game getGameDetailsById(Integer gid) {
                Query query = new Query();
                query.addCriteria(Criteria.where("gid").is(gid));
                return mongoTemplate.findOne(query,
                                Game.class,
                                "games");
        }
}
