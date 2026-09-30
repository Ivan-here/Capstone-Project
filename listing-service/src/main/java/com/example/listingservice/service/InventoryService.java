package com.example.listingservice.service;

import com.example.listingservice.model.Listing;
import com.locally.security.Access;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final MongoTemplate mongo;
    public Listing adjust(String listingId, String operationId, int delta) {
        Access.internal();
        if (operationId == null || !operationId.matches("[A-Za-z0-9:_-]{1,180}") || delta == 0 || delta == Integer.MIN_VALUE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        var criteria = Criteria.where("_id").is(listingId).and("inventoryOperations").ne(operationId);
        if (delta < 0) criteria.and("quantity").gte(-(long) delta);
        else criteria.and("quantity").lte(Integer.MAX_VALUE - delta);
        Listing updated = mongo.findAndModify(Query.query(criteria),
                new Update().inc("quantity", delta).addToSet("inventoryOperations", operationId),
                FindAndModifyOptions.options().returnNew(true), Listing.class);
        if (updated != null) return updated;
        Listing replay = mongo.findOne(Query.query(Criteria.where("_id").is(listingId).and("inventoryOperations").is(operationId)), Listing.class);
        if (replay != null) return replay;
        throw new ResponseStatusException(HttpStatus.CONFLICT, "Insufficient inventory or missing listing");
    }
}
