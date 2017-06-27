/*
 * Copyright 2016 Hannes Dorfmann.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.tlnacl.reactiveapp.businesslogic.feed;


import com.tlnacl.reactiveapp.businesslogic.model.AdditionalItemsLoadable;
import com.tlnacl.reactiveapp.businesslogic.model.FeedItem;
import com.tlnacl.reactiveapp.businesslogic.model.Product;
import com.tlnacl.reactiveapp.businesslogic.model.SectionHeader;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

/**
 * Takes a {@link PagingFeedLoader} but groups the resulting list of products into categories.
 * Assumption: Since we support pagination, we assume that there are not items of the same group on
 * different pages in the data feed retrieved from backend
 *
 * @author Hannes Dorfmann
 */
public class GroupedPagedFeedLoader {
  private final PagingFeedLoader feedLoader;
  private final int collapsedGroupProductItemCount = 3;

  public GroupedPagedFeedLoader(PagingFeedLoader feedLoader) {
    this.feedLoader = feedLoader;
  }

  public Observable<List<FeedItem>> getGroupedFirstPage() {
    return getGroupedNextPage();
  }

  public Observable<List<FeedItem>> getGroupedNextPage() {
    return groupByCategory(feedLoader.nextPage());
  }

  public Observable<List<FeedItem>> getNewestPage() {
    return groupByCategory(feedLoader.newestPage());
  }

  private Observable<List<FeedItem>> groupByCategory(
      Observable<List<Product>> originalListToGroup) {
    return originalListToGroup.flatMap(Observable::fromIterable)
        .groupBy(Product::getCategory)
        .flatMap(groupedByCategory -> groupedByCategory.toList().map(productsInGroup -> {
          String groupName = groupedByCategory.getKey();
          List<FeedItem> items = new ArrayList<FeedItem>();
          items.add(new SectionHeader(groupName));
          if (collapsedGroupProductItemCount < productsInGroup.size()) {
            for (int i = 0; i < collapsedGroupProductItemCount; i++) {
              items.add(productsInGroup.get(i));
            }
            items.add(
                new AdditionalItemsLoadable(productsInGroup.size() - collapsedGroupProductItemCount,
                    groupName, false, null));
          } else {
            items.addAll(productsInGroup);
          }
          return items;
        }).toObservable())
        .concatMap(Observable::fromIterable)
        .toList()
        .toObservable();
  }
}
