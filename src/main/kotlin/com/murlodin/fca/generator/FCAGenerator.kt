import ai.grazie.utils.capitalize
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.murlodin.fca.ui.Notifier
import java.io.File
import java.io.IOException

interface FCAGenerator {
    companion object {



        fun createFolder(
            project: Project,
            folder: VirtualFile,
            parent: String,
            vararg children: String
        ): Map<String, VirtualFile>? {
            try {
                for (child in folder.children) {
                    if (child.name == parent) {
                        Notifier.warning(project, "Directory [$parent] already exists")
                        return null
                    }
                }
                val mapOfFolder = mutableMapOf<String, VirtualFile>()
                mapOfFolder[parent] = folder.createChildDirectory(folder, parent)
                for (child in children) {
                    mapOfFolder[child] =
                        mapOfFolder[parent]?.createChildDirectory(mapOfFolder[parent], child) ?: throw IOException()
                }
                return mapOfFolder
            } catch (e: IOException) {
                Notifier.warning(project, "Couldn't create $parent directory")
                e.printStackTrace()
                return null
            }
        }

        fun createDataFolder(
            project: Project,
            folder: VirtualFile,
        ): Map<String, VirtualFile>? {
            try {
                val mapOfFolder = mutableMapOf<String, VirtualFile>()
                mapOfFolder["data"] = folder.createChildDirectory(folder, "data")
                mapOfFolder["models"] = mapOfFolder["data"]?.createChildDirectory(mapOfFolder["data"], "models") ?: throw IOException()
                mapOfFolder["mappers"] = mapOfFolder["data"]?.createChildDirectory(mapOfFolder["data"], "mappers") ?: throw IOException()
                mapOfFolder["data_sources"] = mapOfFolder["data"]?.createChildDirectory(mapOfFolder["data"], "data_sources") ?: throw IOException()
                mapOfFolder["repositories"] = mapOfFolder["data"]?.createChildDirectory(mapOfFolder["data"], "repositories") ?: throw IOException()
                mapOfFolder["${folder.name}_repository_impl"] = mapOfFolder["repositories"]?.createChildData(mapOfFolder["repositories"],"${folder.name}_repository_impl.dart") ?: throw IOException()
                mapOfFolder["${folder.name}_api_service"] = mapOfFolder["data_sources"]?.createChildData(mapOfFolder["data_sources"],"${folder.name}_api_service.dart") ?: throw IOException()
                mapOfFolder["${folder.name}_dto_mapper"] = mapOfFolder["mappers"]?.createChildData(mapOfFolder["mappers"],"${folder.name}_dto_mapper.dart") ?: throw IOException()


val apiContent = """import "../../../../../../core/api_client/api_client.dart";
    
class ${folder.name.split("_").map { item -> item.capitalize() }.joinToString("")}ApiService {
  ${folder.name.split("_").map { item -> item.capitalize() }.joinToString("")}ApiService({required this.apiClient});

  final ApiClient apiClient;
}    
"""

val repImplContent = """import "../../domain/repositories/${folder.name}_repository.dart";
import "../data_sources/${folder.name}_api_service.dart";

class ${folder.name.split("_").map { item -> item.capitalize() }.joinToString("")}RepositoryImpl implements ${folder.name.split("_").map { item -> item.capitalize() }.joinToString("")}Repository {
  ${folder.name.split("_").map { item -> item.capitalize() }.joinToString("")}RepositoryImpl({required this.${folder.name.split("_").mapIndexed { index, item  -> if (index != 0) item.capitalize() else item }.joinToString("")}ApiService});

  final ${folder.name.split("_").map { item -> item.capitalize() }.joinToString("")}ApiService ${folder.name.split("_").mapIndexed { index, item  -> if (index != 0) item.capitalize() else item }.joinToString("")}ApiService;
}    
"""

                mapOfFolder["${folder.name}_repository_impl"]?.path?.let {
                    File(it).writeText(repImplContent)
                }

                mapOfFolder["${folder.name}_api_service"]?.path?.let {
                    File(it).writeText(apiContent)
                }


                return mapOfFolder
            } catch (e: IOException) {
                Notifier.warning(project, "Couldn't create domain directory")
                e.printStackTrace()
                return null
            }
        }

        fun createDomainFolder(
            project: Project,
            folder: VirtualFile,
        ): Map<String, VirtualFile>? {
            try {
                val mapOfFolder = mutableMapOf<String, VirtualFile>()
                mapOfFolder["domain"] = folder.createChildDirectory(folder, "domain")
                mapOfFolder["entity"] = mapOfFolder["domain"]?.createChildDirectory(mapOfFolder["domain"], "entity") ?: throw IOException()
                mapOfFolder["use_cases"] = mapOfFolder["domain"]?.createChildDirectory(mapOfFolder["domain"], "use_cases") ?: throw IOException()
                mapOfFolder["repositories"] = mapOfFolder["domain"]?.createChildDirectory(mapOfFolder["domain"], "repositories") ?: throw IOException()
                mapOfFolder["${folder.name}_repository"] = mapOfFolder["repositories"]?.createChildData(mapOfFolder["repositories"],"${folder.name}_repository.dart") ?: throw IOException()
                mapOfFolder["${folder.name}_repository"]?.path?.let {
                    File(it).writeText("abstract interface class ${folder.name.split("_").map { item -> item.capitalize() }.joinToString("")}Repository {  }")
                }
                return mapOfFolder
            } catch (e: IOException) {
                Notifier.warning(project, "Couldn't create domain directory")
                e.printStackTrace()
                return null
            }
        }

        fun createPresentationFolder(
            project: Project,
            folder: VirtualFile,
        ): Map<String, VirtualFile>? {
            try {
                val mapOfFolder = mutableMapOf<String, VirtualFile>()
                mapOfFolder["presentation"] = folder.createChildDirectory(folder, "presentation")
                mapOfFolder["pages"] = mapOfFolder["presentation"]?.createChildDirectory(mapOfFolder["presentation"], "pages") ?: throw IOException()
                mapOfFolder["widgets"] = mapOfFolder["presentation"]?.createChildDirectory(mapOfFolder["presentation"], "widgets") ?: throw IOException()
                mapOfFolder["bloc"] = mapOfFolder["presentation"]?.createChildDirectory(mapOfFolder["presentation"], "bloc") ?: throw IOException()
                mapOfFolder["${folder.name}_bloc"] = mapOfFolder["bloc"]?.createChildData(mapOfFolder["bloc"],"${folder.name}_bloc.dart") ?: throw IOException()
                mapOfFolder["${folder.name}_state"] = mapOfFolder["bloc"]?.createChildData(mapOfFolder["bloc"],"${folder.name}_state.dart") ?: throw IOException()
                mapOfFolder["${folder.name}_event"] = mapOfFolder["bloc"]?.createChildData(mapOfFolder["bloc"],"${folder.name}_event.dart") ?: throw IOException()


                val blocContent =
"""import "package:bloc/bloc.dart";

part "${folder.name}_event.dart";
part "${folder.name}_state.dart";

class ${folder.name.split("_").map { item -> item.capitalize() }.joinToString("")}Bloc extends Bloc<${folder.name.split("_").map { item -> item.capitalize() }.joinToString("")}Event, ${folder.name.split("_").map { item -> item.capitalize() }.joinToString("")}State> {
  ${folder.name.split("_").map { item -> item.capitalize() }.joinToString("")}Bloc() : super(${folder.name.split("_").map { item -> item.capitalize() }.joinToString("")}State()) {
    on<${folder.name.split("_").map { item -> item.capitalize() }.joinToString("")}Event>((event, emit) {

    });
  }
}       
"""
                val stateContent =
"""part of '${folder.name}_bloc.dart';

enum  ${folder.name.split("_").map { item -> item.capitalize() }.joinToString("")}StateStatus {
  init,
  loading,
  success,
  error,
}


extension ${folder.name.split("_").map { item -> item.capitalize() }.joinToString("")}CheckStatus on ${folder.name.split("_").map { item -> item.capitalize() }.joinToString("")}StateStatus {
  bool get isInit => this == ${folder.name.split("_").map { item -> item.capitalize() }.joinToString("")}StateStatus.init;
  bool get isLoading => this == ${folder.name.split("_").map { item -> item.capitalize() }.joinToString("")}StateStatus.loading;
  bool get isSuccess => this == ${folder.name.split("_").map { item -> item.capitalize() }.joinToString("")}StateStatus.success;
  bool get isError => this == ${folder.name.split("_").map { item -> item.capitalize() }.joinToString("")}StateStatus.error;
}

class ${folder.name.split("_").map { item -> item.capitalize() }.joinToString("")}State {
  ${folder.name.split("_").map { item -> item.capitalize() }.joinToString("")}State({
    this.status = ${folder.name.split("_").map { item -> item.capitalize() }.joinToString("")}StateStatus.init,
  });

  final ${folder.name.split("_").map { item -> item.capitalize() }.joinToString("")}StateStatus status;

  ${folder.name.split("_").map { item -> item.capitalize() }.joinToString("")}State copyWith({
    ${folder.name.split("_").map { item -> item.capitalize() }.joinToString("")}StateStatus? status,
  }) {
    return ${folder.name.split("_").map { item -> item.capitalize() }.joinToString("")}State(
      status: status ?? this.status,
    );
  }

}
"""

                val eventContent =
"""part of '${folder.name}_bloc.dart';

sealed class ${folder.name.split("_").map { item -> item.capitalize() }.joinToString("")}Event {}   
"""


                mapOfFolder["${folder.name}_bloc"]?.path?.let {
                    File(it).writeText(blocContent)
                }
                mapOfFolder["${folder.name}_state"]?.path?.let {
                    File(it).writeText(stateContent)
                }
                mapOfFolder["${folder.name}_event"]?.path?.let {
                    File(it).writeText(eventContent)
                }
                return mapOfFolder
            } catch (e: IOException) {
                Notifier.warning(project, "Couldn't create domain directory")
                e.printStackTrace()
                return null
            }
        }



    }
}
