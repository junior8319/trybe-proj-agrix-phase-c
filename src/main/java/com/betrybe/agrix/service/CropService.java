package com.betrybe.agrix.service;

import com.betrybe.agrix.entity.Crop;
import com.betrybe.agrix.entity.Farm;
import com.betrybe.agrix.entity.Fertilizer;
import com.betrybe.agrix.repository.CropRepository;
import com.betrybe.agrix.service.exception.CropNotFoundException;
import com.betrybe.agrix.service.exception.FarmNotFoundException;
import com.betrybe.agrix.service.exception.FertilizerNotFoundException;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The type Crop service.
 */
@Service
public class CropService {
  private final CropRepository cropRepository;
  private final FarmService farmService;
  private final FertilizerService fertilizerService;

  /**
   * Instantiates a new Crop service.
   *
   * @param cropRepository    the crop repository
   * @param farmService       the farm service
   * @param fertilizerService the fertilizer service
   */
  @Autowired
  public CropService(CropRepository cropRepository, FarmService farmService,
      FertilizerService fertilizerService) {
    this.cropRepository = cropRepository;
    this.farmService = farmService;
    this.fertilizerService = fertilizerService;
  }

  /**
   * Find by id crop.
   *
   * @param id the id
   * @return the crop
   * @throws CropNotFoundException the crop not found exception
   */
  public Crop findById(Long id) throws CropNotFoundException {
    return cropRepository.findById(id)
        .orElseThrow(CropNotFoundException::new);
  }

  /**
   * Find all list.
   *
   * @return the list
   */
  public List<Crop> findAll() {
    return cropRepository.findAll();
  }

  /**
   * Create crop.
   *
   * @param cropToSave the crop to save
   * @return the crop
   */
  public Crop create(Crop cropToSave) {
    return cropRepository.save(cropToSave);
  }

  /**
   * Update crop.
   *
   * @param id              the id
   * @param cropWithChanges the crop with changes
   * @return the crop
   * @throws CropNotFoundException the crop not found exception
   * @throws FarmNotFoundException the farm not found exception
   */
  public Crop update(Long id, Crop cropWithChanges)
      throws CropNotFoundException, FarmNotFoundException {
    Crop cropToChange = findById(id);

    if (!cropWithChanges.getName().isEmpty() && !cropWithChanges.getName().isBlank()) {
      cropToChange.setName(cropWithChanges.getName());
    }

    if (!cropWithChanges.getPlantedArea().isNaN() && !(cropWithChanges.getPlantedArea() == null)) {
      cropToChange.setplantedArea(cropWithChanges.getPlantedArea());
    }

    if (!(cropWithChanges.getFarmId() == null)) {
      Farm farmToVinculate = farmService.findById(cropWithChanges.getFarmId());
      cropToChange.setFarm(farmToVinculate);
    }

    return cropRepository.save(cropToChange);
  }

  /**
   * Delete by id crop.
   *
   * @param id the id
   * @return the crop
   * @throws CropNotFoundException the crop not found exception
   */
  public Crop deleteById(Long id) throws CropNotFoundException {
    Crop cropToExclude = findById(id);

    cropRepository.deleteById(id);

    return cropToExclude;
  }

  /**
   * Sets crop farm.
   *
   * @param cropId the crop id
   * @param farmId the farm id
   * @return the crop farm
   * @throws CropNotFoundException the crop not found exception
   * @throws FarmNotFoundException the farm not found exception
   */
  public Crop setCropFarm(
      Long cropId,
      Long farmId
  ) throws CropNotFoundException, FarmNotFoundException {
    Crop crop = findById(cropId);
    Farm farm = farmService.findById(farmId);

    crop.setFarm(farm);

    return cropRepository.save(crop);
  }

  /**
   * Sets fertilizer crop.
   *
   * @param cropId       the crop id
   * @param fertilizerId the fertilizer id
   * @return a success message or
   * @throws CropNotFoundException       the crop not found exception
   * @throws FertilizerNotFoundException the fertilizer not found exception
   */
  public String setFertilizerCrop(
      Long cropId,
      Long fertilizerId
  ) throws CropNotFoundException, FertilizerNotFoundException {
    Crop cropToAssociate = findById(cropId);
    Fertilizer fertilizerToAssociate = fertilizerService.getFertilizerById(fertilizerId);

    cropToAssociate.getFertilizers().add(fertilizerToAssociate);

    cropRepository.save(cropToAssociate);

    return "Fertilizante e plantação associados com sucesso!";
  }

  /**
   * Remove crop farm crop.
   *
   * @param cropId the crop id
   * @return the crop
   * @throws CropNotFoundException the crop not found exception
   */
  public Crop removeCropFarm(Long cropId) throws CropNotFoundException {
    Crop crop = findById(cropId);

    crop.setFarm(null);

    return cropRepository.save(crop);
  }

  /**
   * Gets crop by harvest date interval.
   *
   * @param start the start
   * @param end   the end
   * @return the crop by harvest date interval
   */
  public List<Crop> getCropByHarvestDateInterval(LocalDate start, LocalDate end) {
    List<Crop> crops = findAll();

    return crops.stream()
        .filter(crop -> crop.getHarvestDate().isEqual(start)
            || crop.getHarvestDate().isAfter(start))
        .filter(crop -> crop.getHarvestDate().isBefore(end)
            || crop.getHarvestDate().isEqual(end))
        .toList();
  }

  /**
   * Gets crop fertilizers.
   *
   * @param id the id
   * @return the crop fertilizers
   * @throws CropNotFoundException the crop not found exception
   */
  public List<Fertilizer> getCropFertilizers(Long id) throws CropNotFoundException {
    return findById(id).getFertilizers();
  }
}
