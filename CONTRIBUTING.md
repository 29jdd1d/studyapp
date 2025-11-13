# 贡献指南 (Contributing Guide)

感谢您对考研学习小程序项目的关注！我们欢迎任何形式的贡献。

Thank you for your interest in the Postgraduate Study Mini Program project! We welcome all forms of contributions.

## 如何贡献 (How to Contribute)

### 报告问题 (Reporting Issues)

如果您发现了bug或有功能建议，请：

If you find a bug or have a feature suggestion, please:

1. 检查 [Issues](https://github.com/29jdd1d/studyapp/issues) 确认问题未被报告
2. 创建一个新的 Issue，详细描述问题或建议
3. 如果可能，提供复现步骤或示例代码

### 提交代码 (Submitting Code)

#### 1. Fork 项目

点击页面右上角的 "Fork" 按钮，将项目 fork 到你的账号下。

Click the "Fork" button in the upper right corner to fork the project to your account.

#### 2. 克隆仓库

```bash
git clone https://github.com/YOUR_USERNAME/studyapp.git
cd studyapp
```

#### 3. 创建分支

```bash
git checkout -b feature/your-feature-name
# 或者 (or)
git checkout -b fix/your-bug-fix
```

分支命名规范 (Branch naming conventions):
- `feature/xxx` - 新功能
- `fix/xxx` - Bug修复
- `docs/xxx` - 文档更新
- `refactor/xxx` - 代码重构
- `test/xxx` - 测试相关

#### 4. 开发

##### 环境设置

确保你已安装：
- JDK 1.8+
- Maven 3.6+
- MySQL 8.0+ (或使用 Docker)
- Git

##### 运行项目

```bash
# 使用 Docker (推荐)
docker-compose up -d

# 或传统方式
mvn spring-boot:run
```

##### 代码规范

- 使用 4 个空格缩进（Java）
- 使用 2 个空格缩进（YAML, JSON, JavaScript）
- 遵循 Google Java Style Guide
- 添加适当的注释（中英文双语更佳）
- 确保所有测试通过

示例：

```java
/**
 * 获取用户信息
 * Get user information
 * 
 * @param userId 用户ID (User ID)
 * @return 用户信息 (User information)
 */
public UserDTO getUserInfo(Long userId) {
    // 验证参数 (Validate parameters)
    if (userId == null || userId <= 0) {
        throw new IllegalArgumentException("用户ID不能为空 (User ID cannot be null)");
    }
    
    // 查询用户 (Query user)
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User", userId));
    
    // 返回结果 (Return result)
    return convertToDTO(user);
}
```

#### 5. 提交更改

```bash
# 添加更改
git add .

# 提交（使用有意义的提交信息）
git commit -m "feat: add user profile API"
```

提交信息规范 (Commit message conventions):

```
<type>: <subject>

<body>

<footer>
```

类型 (Types):
- `feat`: 新功能
- `fix`: Bug修复
- `docs`: 文档更新
- `style`: 代码格式（不影响代码运行）
- `refactor`: 重构
- `test`: 测试相关
- `chore`: 构建过程或辅助工具的变动

示例 (Examples):
```
feat: add user authentication API
fix: resolve null pointer in login service
docs: update deployment guide
test: add unit tests for user service
```

#### 6. 运行测试

```bash
# 运行所有测试
mvn test

# 运行特定测试
mvn test -Dtest=UserServiceTest

# 检查代码覆盖率
mvn clean test jacoco:report
```

确保：
- ✅ 所有测试通过
- ✅ 代码覆盖率不降低
- ✅ 没有编译警告

#### 7. 推送到 GitHub

```bash
git push origin feature/your-feature-name
```

#### 8. 创建 Pull Request

1. 访问你 fork 的仓库页面
2. 点击 "New Pull Request"
3. 选择你的分支
4. 填写 PR 标题和描述
5. 提交 PR

PR 描述应包含：
- 改动的目的和背景
- 主要更改内容
- 测试结果
- 相关 Issue 编号（如果有）

示例：

```markdown
## 目的 (Purpose)
添加用户个人资料管理API

## 更改内容 (Changes)
- 添加 UserProfileController
- 实现用户信息更新功能
- 添加头像上传功能
- 添加单元测试

## 测试 (Testing)
- [x] 单元测试通过
- [x] 集成测试通过
- [x] 手动测试通过

## 相关 Issue
Closes #123
```

## 开发指南 (Development Guidelines)

### 目录结构

```
src/main/java/com/studyapp/
├── config/          # 配置类
├── controller/      # 控制器
├── service/         # 服务层
├── repository/      # 数据访问层
├── entity/          # 实体类
├── dto/             # 数据传输对象
├── exception/       # 自定义异常
├── util/            # 工具类
└── common/          # 通用类
```

### 新增功能

#### 1. 创建实体类 (Entity)

```java
@Entity
@Table(name = "your_table")
@Data
public class YourEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // ... other fields
}
```

#### 2. 创建 Repository

```java
public interface YourRepository extends JpaRepository<YourEntity, Long> {
    // 自定义查询方法
}
```

#### 3. 创建 Service

```java
@Service
public class YourService {
    @Autowired
    private YourRepository repository;
    
    // 业务逻辑
}
```

#### 4. 创建 Controller

```java
@RestController
@RequestMapping("/your-path")
@Api(tags = "Your API")
public class YourController {
    @Autowired
    private YourService service;
    
    @GetMapping
    public Result<List<YourDTO>> list() {
        // ...
    }
}
```

#### 5. 添加测试

```java
@SpringBootTest
@ActiveProfiles("test")
public class YourServiceTest {
    @Autowired
    private YourService service;
    
    @Test
    public void testYourMethod() {
        // ...
    }
}
```

### 数据库迁移

如果需要修改数据库结构：

1. 更新实体类
2. JPA 会自动更新表结构（开发环境）
3. 生产环境需要手动执行 SQL 脚本

### API 文档

使用 Swagger 注解：

```java
@ApiOperation("获取用户信息")
@ApiImplicitParam(name = "id", value = "用户ID", required = true)
public Result<UserDTO> getUser(@PathVariable Long id) {
    // ...
}
```

访问 Swagger UI: http://localhost:8080/swagger-ui/

## 代码审查 (Code Review)

PR 会经过以下检查：

1. ✅ 代码风格符合规范
2. ✅ 所有测试通过
3. ✅ 没有安全漏洞
4. ✅ 文档更新（如果需要）
5. ✅ 功能符合需求

审查可能需要几天时间，请保持耐心。

## 社区规范 (Community Guidelines)

- 尊重所有贡献者
- 保持友好和建设性的讨论
- 遵循代码规范
- 及时响应评审意见

## 获取帮助 (Getting Help)

如有问题，可以：

1. 查看项目文档
2. 搜索已有的 Issues
3. 在 Discussions 中提问
4. 创建新的 Issue

## 许可证 (License)

提交代码即表示您同意将代码以 MIT 许可证开源。

---

再次感谢您的贡献！🎉

Thank you again for your contribution! 🎉
