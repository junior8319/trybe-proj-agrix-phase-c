package com.betrybe.agrix.controller;

import com.betrybe.agrix.controller.dto.CropDto;
import com.betrybe.agrix.controller.dto.FertilizerDto;
import com.betrybe.agrix.entity.Crop;
import com.betrybe.agrix.service.CropService;
import com.betrybe.agrix.service.exception.CropNotFoundException;
import com.betrybe.agrix.service.exception.FertilizerNotFoundException;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * The type Crop controller.
 */
@RestController
@RequestMapping("/crops")
public class CropController {
  private final CropService cropService;

  /**
   * Instantiates a new Crop controller.
   *
   * @param cropService the crop service
   */
  @Autowired
  public CropController(CropService cropService) {
    this.cropService = cropService;
  }

  /**
   * Gets crop by id.
   *
   * @param id the id
   * @return the crop by id
   * @throws CropNotFoundException the crop not found exception
   */
  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public CropDto getCropById(@PathVariable Long id) throws CropNotFoundException {
    return CropDto.fromEntity(cropService.findById(id));
  }

  /**
   * Gets all crops.
   *
   * @return the all crops
   */
  @GetMapping
  @PreAuthorize(
      "hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_MANAGER')")
  @ResponseStatus(HttpStatus.OK)
  public List<CropDto> getAllCrops() {
    return cropService.findAll()
        .stream()
        .map(CropDto::fromEntity)
        .toList();
  }

  /**
   * Gets crops by harvest dates interval.
   *
   * @param start the start
   * @param end   the end
   * @return the crops by harvest dates interval
   */
  @GetMapping("/search")
  @ResponseStatus(HttpStatus.OK)
  public List<CropDto> getCropsByHarvestDatesInterval(
      @RequestParam LocalDate start,
      @RequestParam LocalDate end
  ) {
    List<Crop> crops = cropService.getCropByHarvestDateInterval(start, end);

    return crops.stream().map(CropDto::fromEntity).toList();
  }

  /**
   * Gets crop fertilizers by crop id.
   *
   * @param cropId the crop id
   * @return the crop fertilizers by crop id
   * @throws CropNotFoundException the crop not found exception
   */
  @GetMapping("/{cropId}/fertilizers")
  @ResponseStatus(HttpStatus.OK)
  public List<FertilizerDto> getCropFertilizersByCropId(@PathVariable Long cropId)
      throws CropNotFoundException {
    return cropService.getCropFertilizers(cropId)
        .stream()
        .map(FertilizerDto::fromEntity)
        .toList();
  }

  /**
   * Sets crop fertilizer.
   *
   * @param cropId       the crop id
   * @param fertilizerId the fertilizer id
   * @return the crop fertilizer
   * @throws CropNotFoundException       the crop not found exception
   * @throws FertilizerNotFoundException the fertilizer not found exception
   */
  @PostMapping("/{cropId}/fertilizers/{fertilizerId}")
  @ResponseStatus(HttpStatus.CREATED)
  public String setCropFertilizer(@PathVariable Long cropId, @PathVariable Long fertilizerId)
      throws CropNotFoundException, FertilizerNotFoundException {
    return cropService.setFertilizerCrop(cropId, fertilizerId);
  }
}
