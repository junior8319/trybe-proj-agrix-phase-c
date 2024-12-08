package com.betrybe.agrix.controller;

import com.betrybe.agrix.controller.dto.CropCreationDto;
import com.betrybe.agrix.controller.dto.CropDto;
import com.betrybe.agrix.controller.dto.FarmCreationDto;
import com.betrybe.agrix.controller.dto.FarmDto;
import com.betrybe.agrix.entity.Crop;
import com.betrybe.agrix.entity.Farm;
import com.betrybe.agrix.service.CropService;
import com.betrybe.agrix.service.FarmService;
import com.betrybe.agrix.service.exception.CropNotFoundException;
import com.betrybe.agrix.service.exception.FarmNotFoundException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * The type Farm controller.
 */
@RestController
@RequestMapping("/farms")
public class FarmController {
  private final FarmService service;
  private final CropService cropService;

  /**
   * Instantiates a new Farm controller.
   *
   * @param service     the service
   * @param cropService the crop service
   */
  @Autowired
  public FarmController(FarmService service, CropService cropService) {
    this.service = service;
    this.cropService = cropService;
  }

  /**
   * Gets all farms.
   *
   * @return the all farms
   */
  @GetMapping
  @PreAuthorize(
      "hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_USER')")
  @ResponseStatus(HttpStatus.OK)
  public List<FarmDto> getAllFarms() {
    return service.findAll()
        .stream()
        .map(FarmDto::fromEntity)
        .toList();
  }

  /**
   * Gets farm by id.
   *
   * @param id the id
   * @return the farm by id
   * @throws FarmNotFoundException the farm not found exception
   */
  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public FarmDto getFarmById(@PathVariable Long id) throws FarmNotFoundException {
    return FarmDto.fromEntity(service.findById(id));
  }

  /**
   * Gets crops from farm by farm id.
   *
   * @param id the id
   * @return the crops from farm by farm id
   * @throws FarmNotFoundException the farm not found exception
   */
  @GetMapping("/{id}/crops")
  @ResponseStatus(HttpStatus.OK)
  public List<CropDto> getCropsFromFarmByFarmId(
      @PathVariable Long id
  ) throws FarmNotFoundException {
    return service.getCrops(id)
        .stream()
        .map(CropDto::fromEntity)
        .toList();
  }

  /**
   * Create farm dto.
   *
   * @param farmCreationDto the farm creation dto
   * @return the farm dto
   */
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public FarmDto createFarm(@RequestBody FarmCreationDto farmCreationDto) {
    return FarmDto.fromEntity(service.create(farmCreationDto.toEntity()));
  }

  /**
   * Create crop to farm crop dto.
   *
   * @param farmId          the farm id
   * @param cropCreationDto the crop creation dto
   * @return the crop dto
   * @throws FarmNotFoundException the farm not found exception
   * @throws CropNotFoundException the crop not found exception
   */
  @PostMapping("/{farmId}/crops")
  @ResponseStatus(HttpStatus.CREATED)
  public CropDto createCropToFarm(
      @PathVariable Long farmId,
      @RequestBody CropCreationDto cropCreationDto
  ) throws FarmNotFoundException, CropNotFoundException {
    Crop cropToSave = new Crop(
        cropCreationDto.name(),
        cropCreationDto.plantedArea(),
        cropCreationDto.plantedDate(),
        cropCreationDto.harvestDate()
    );

    Farm farmToVinculate = service.findById(farmId);
    Crop cropCreated = cropService.create(cropToSave);
    cropCreated.setFarm(farmToVinculate);

    cropService.setCropFarm(cropCreated.getId(), cropCreated.getFarmId());

    return CropDto.fromEntity(cropCreated);
  }
}
