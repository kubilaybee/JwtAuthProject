package com.auth.JWTAuth.util;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.data.domain.Page;

public class ModelMapperUtils {
  private static final ModelMapper modelMapper;

  static {
    modelMapper = new ModelMapper();
    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    modelMapper.getConfiguration().setSkipNullEnabled(true);
  }

  private ModelMapperUtils() {}

  public static <S, D> D map(final S source, D destination) throws Exception {
    try {
      if (source == null) {
        return null;
      }
      modelMapper.map(source, destination);
      return destination;
    } catch (Exception e) {
      throw new Exception(
          "Exception occurred at ModelMapperUtils map : " + e.getCause() + e.getMessage());
    }
  }

  public static <D, T> D map(final T source, Class<D> targetClass) {
    if (source == null) {
      return null;
    }
    return modelMapper.map(source, targetClass);
  }

  public static <D, T> List<D> mapAll(final Collection<T> sources, Class<D> targetClass) {
    return sources.isEmpty()
        ? Collections.emptyList()
        : sources.stream().map(source -> map(source, targetClass)).collect(Collectors.toList());
  }

  public static <D, T> Page<D> mapPage(Page<T> sourcePage, Class<D> targetClass) {
    return sourcePage.isEmpty() ? Page.empty() : sourcePage.map(source -> map(source, targetClass));
  }
}
