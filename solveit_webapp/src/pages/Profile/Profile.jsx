import './Profile.css';
import { useState, useEffect, useContext, useCallback } from 'react';
import Sidebar from "../../components/Sidebar/Sidebar.jsx";
import { AppContext } from "../../context/AppContext.jsx";
import AxiosConfig from "../../service/AxiosConfig.js";
import { API_ENDPOINTS } from "../../service/ApiEndpoints.js";
import { formatTimePost } from "../../util/UtilMethod.js";
import { toast } from "react-toastify";

const Profile = () => {
    const { user } = useContext(AppContext);

    const [userQuestions, setUserQuestions] = useState([]);
    const [questionData, setQuestionData] = useState(null);
    const [answerInput, setAnswerInput] = useState('');
    const [image, setImage] = useState(null);
    const [imageFile, setImageFile] = useState(null);
    const [data, setData] = useState({
        title: '',
        description: ''
    });

    const [loading, setLoading] = useState(true);
    const [showEditModal, setShowEditModal] = useState(false);
    const [showDetailModal, setShowDetailModal] = useState(false);
    const [selectedQuestion, setSelectedQuestion] = useState(null);

    const fetchUserQuestions = useCallback(async () => {
        try {
            setLoading(true);
            const response = await AxiosConfig.get(API_ENDPOINTS.GET_QUESTIONS_CURRENT_USER);
            console.log(response.data.content);
            if (response.status === 200) {
                const data = response.data.content;
                setUserQuestions(data);
            }
        } catch (error) {
            console.error("Error fetching questions:", error);
        } finally {
            setLoading(false);
        }
    }, []);

    const fetchAnswers = async (questionId) => {
        try {
            const response = await AxiosConfig.get(API_ENDPOINTS.GET_QUESTION(questionId));
            if (response.status === 200) {
                setQuestionData(response.data);
            }
        } catch (error) {
            console.error("Error fetching answers:", error);
        }
    };

    useEffect(() => {
        if (user) {
            fetchUserQuestions();
        }
    }, [user, fetchUserQuestions]);

    const handleLikesQuestion = async (e, questionId) => {
        e.stopPropagation();
        try {
            const response = await AxiosConfig.post(API_ENDPOINTS.LIKE_QUESTION(questionId));
            if (response.status === 201 || response.status === 200) {
                fetchUserQuestions();
            }
        } catch (error) {
            console.error("Error liking question:", error);
        }
    };

    const handleUnLikesQuestion = async (e, questionId) => {
        e.stopPropagation();
        try {
            const response = await AxiosConfig.post(API_ENDPOINTS.UNLIKE_QUESTION(questionId));
            if (response.status === 201 || response.status === 200) {
                fetchUserQuestions();
            }
        } catch (error) {
            console.error("Error unliking question:", error);
        }
    };

    const handleQuestionClick = (question) => {
        setSelectedQuestion(question);
        setShowDetailModal(true);
        fetchAnswers(question.id);
    };

    const openEditModal = (e, question) => {
        e.stopPropagation();
        setSelectedQuestion(question);
        setData({
            title: question.title || '',
            description: question.description || ''
        });
        setImage(question.imageUrl || null);
        setShowEditModal(true);
    };

    const closeEditModal = () => {
        setShowEditModal(false);
        setSelectedQuestion(null);
        setData({ title: '', description: '' });
        setImage(null);
        setImageFile(null);
    };

    const closeDetailModal = () => {
        setShowDetailModal(false);
        setSelectedQuestion(null);
        setQuestionData(null);
        setAnswerInput('');
    };

    const handleImageChange = (e) => {
        if (e.target.files && e.target.files[0]) {
            setImage(URL.createObjectURL(e.target.files[0]));
            setImageFile(e.target.files[0]);
        }
    };

    const handleUpdate = async (e) => {
        e.preventDefault();
        setLoading(true);

        const formData = new FormData();
        if (imageFile) {
            formData.append('image', imageFile);
        }

        formData.append('request', JSON.stringify(data));

        try {
            const response = await AxiosConfig.patch(API_ENDPOINTS.UPDATE_QUESTION(selectedQuestion.id), formData);

            if (response.status === 200) {
                toast.success('Question successfully updated.');
                fetchUserQuestions();
                closeEditModal();
            }
        } catch (error) {
            console.error(error);
            toast.error('Question failed to update');
        } finally {
            setLoading(false);
        }
    };

    const handleDelete = async () => {
        try {
            const response = await AxiosConfig.delete(API_ENDPOINTS.DELETE_QUESTION(selectedQuestion.id));
            if (response.status === 200) {
                toast.success("Successfully deleted question");
                fetchUserQuestions();
                closeEditModal();
            }
        } catch (error) {
            console.error("Error deleting question:", error);
            toast.error("Failed to delete question");
        }
    };

    const handleSubmitAnswer = async (e) => {
        e.preventDefault();
        if (!answerInput.trim()) return;

        try {
            const response = await AxiosConfig.post(API_ENDPOINTS.ANSWERS_QUESTION, {
                description: answerInput,
                questionId: selectedQuestion.id
            });

            if (response.status === 201) {
                toast.success("Answer added successfully!");
                setAnswerInput('');
                fetchAnswers(selectedQuestion.id);
            }
        } catch (error) {
            console.error("Error submitting answer:", error);
            toast.error("Failed to submit answer");
        }
    };

    if (!user) {
        return (
            <div className="explore-container">
                <Sidebar />
                <main className="explore-main">
                    <div className="loading-state">
                        <div className="spinner"></div>
                        <p>Loading profile...</p>
                    </div>
                </main>
            </div>
        );
    }

    return (
        <div className="explore-container">
            <Sidebar />

            <main className="explore-main">
                <div className="profile-header-section">
                    <div className="profile-top">
                        <div className="profile-avatar-large">
                            <i className="bi bi-person-circle"></i>
                        </div>
                        <div className="profile-text-info">
                            <h1 className="profile-real-name">{user.name}</h1>
                            <p className="profile-handle">{user.username}</p>
                            <p className="profile-bio">{user.bio}</p>
                        </div>
                    </div>

                    <div className="profile-stats-bar">
                        <div className="stat-card">
                            <span className="stat-value">{user.questions}</span>
                            <span className="stat-label">Questions</span>
                        </div>
                        <div className="stat-card">
                            <span className="stat-value">{user.answers}</span>
                            <span className="stat-label">Answers</span>
                        </div>
                    </div>
                </div>

                <hr className="profile-divider" />
                <h2 className="section-title">My Questions</h2>

                <div className="questions-feed">
                    {loading && userQuestions.length === 0 ? (
                        <div className="loading-state">
                            <div className="spinner"></div>
                        </div>
                    ) : userQuestions.length === 0 ? (
                        <div className="empty-state">
                            <p>You haven't asked any questions yet.</p>
                        </div>
                    ) : (
                        userQuestions.map((question) => (
                            <div key={question.id}
                                className="question-card"
                                onClick={() => handleQuestionClick(question)}>

                                <div className="question-header">
                                    <div className="author-info">
                                        <div className="author-details">
                                            <span className="author-name">{user.name}</span>
                                            <span className="author-username">{user.username}</span>
                                        </div>
                                    </div>
                                    <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                                        <span className="time-ago">{formatTimePost(question.createdAt)}</span>
                                        <button className="more-options-btn"
                                            onClick={(e) => openEditModal(e, question)}>
                                            <i className="bi bi-three-dots-vertical"></i>
                                        </button>
                                    </div>
                                </div>

                                <h3 className="question-title">{question.title}</h3>
                                {question.description && (
                                    <p className="question-content">{question.description}</p>
                                )}
                                {question.imageUrl && (
                                    <div className="question-image">
                                        <img src={question.imageUrl} alt="Question visual" />
                                    </div>
                                )}
                                <div className="question-stats">
                                    <div className="stat-item">
                                        <i className="bi bi-chat-left-text"></i>
                                        <span>{question.answers ?? 0}</span>
                                    </div>

                                    <div className={`stat-item likes ${question.liked ? 'active' : ''}`}
                                        onClick={(e) => handleLikesQuestion(e, question.id)}>
                                        <i className={`bi ${question.liked ? 'bi-hand-thumbs-up-fill' : 'bi-hand-thumbs-up'}`}></i>
                                        <span>{question.likes || 0}</span>
                                    </div>

                                    <div className={`stat-item dislikes ${question.unliked ? 'active' : ''}`}
                                        onClick={(e) => handleUnLikesQuestion(e, question.id)}>
                                        <i className={`bi ${question.unliked ? 'bi-hand-thumbs-down-fill' : 'bi-hand-thumbs-down'}`}></i>
                                        <span>{question.unlikes || 0}</span>
                                    </div>
                                </div>
                            </div>
                        ))
                    )}
                </div>
            </main>

            {/* Edit Modal */}
            {showEditModal && (
                <div className="modal-overlay" onClick={closeEditModal}>
                    <div className="modal-content" onClick={(e) => e.stopPropagation()}>
                        <div className="modal-header">
                            <h3>Edit Question</h3>
                            <button className="modal-close" onClick={closeEditModal}>
                                <i className="bi bi-x-lg"></i>
                            </button>
                        </div>
                        <div className="modal-body">
                            <form className="ask-question-form" onSubmit={handleUpdate}>
                                <div className="form-group">
                                    <label htmlFor="title">Title</label>
                                    <p className="label-desc">
                                        Be specific and imagine you're asking a question to another person.
                                    </p>
                                    <input type="text"
                                        id="title"
                                        placeholder="e.g. Is there an R function for finding the index of an element in a vector?"
                                        value={data.title}
                                        onChange={(e) => setData({ ...data, title: e.target.value })}
                                        required/>
                                </div>

                                <div className="form-group">
                                    <label htmlFor="description">Description</label>
                                    <p className="label-desc">
                                        Include all the information someone would need to answer your question.
                                    </p>
                                    <textarea id="description"
                                        rows="8"
                                        placeholder="Write your details here..."
                                        value={data.description}
                                        onChange={(e) => setData({ ...data, description: e.target.value })}
                                        required>
                                    </textarea>
                                </div>

                                <div className="form-group">
                                    <label>
                                        Image <span className="optional-tag">(Optional)</span>
                                    </label>
                                    <div className="image-upload-wrapper">
                                        <label htmlFor="image-input" className="image-upload-label">
                                            <i className="bi bi-image"></i>
                                            <span>
                                                {image ? "Change Image" : "Upload an image to illustrate your problem"}
                                            </span>
                                        </label>
                                        <input
                                            type="file"
                                            id="image-input"
                                            accept="image/*"
                                            onChange={handleImageChange}
                                            hidden
                                        />
                                    </div>

                                    {image && (
                                        <div className="image-preview">
                                            <img src={image} alt="Preview" />
                                            <button type="button"
                                                onClick={() => {
                                                    setImage(null);
                                                    setImageFile(null);
                                                }}
                                                className="remove-img-btn">
                                                <i className="bi bi-x-circle-fill"></i>
                                            </button>
                                        </div>
                                    )}
                                </div>

                                <div className="modal-footer">
                                    <button type="submit" className="modal-btn update-btn">
                                        <i className="bi bi-pencil-square"></i>
                                        Update Question
                                    </button>
                                    <button type="button" className="modal-btn delete-btn" onClick={handleDelete}>
                                        <i className="bi bi-trash"></i>
                                        Delete Question
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            )}

            {/* Question Detail Modal */}
            {showDetailModal && selectedQuestion && questionData && (
                <div className="modal-overlay" onClick={closeDetailModal}>
                    <div className="modal-content-question" onClick={(e) => e.stopPropagation()}>
                        <div className="modal-header">
                            <h3> &nbsp; &nbsp;&nbsp;Question Details</h3>
                            <button className="modal-close" onClick={closeDetailModal}>
                                <i className="bi bi-x-lg"></i>
                            </button>
                        </div>

                        <hr className="answer-divider"/>

                        <div className="modal-body-answers">
                            <div className="question-detail">
                                <div className="question-header">
                                    <div className="author-info">
                                        <div className="author-details">
                                            <span className="author-name">{questionData.author.name || 'Anonymous'}</span>
                                            <span className="author-username">@{questionData.author.username || 'user'}</span>
                                        </div>
                                    </div>
                                    <span className="time-ago">{formatTimePost(questionData.createdAt)}</span>
                                </div>

                                <h3 className="question-title">{questionData.title}</h3>
                                {questionData.description && (
                                    <p className="question-content">{questionData.description}</p>
                                )}
                                {questionData.imageUrl && (
                                    <div className="question-image">
                                        <img src={questionData.imageUrl} alt="Question visual" />
                                    </div>
                                )}
                            </div>

                            <form className="answer-form" onSubmit={handleSubmitAnswer}>
                                <input
                                    type="text"
                                    className="answer-input"
                                    placeholder="Write your answer..."
                                    value={answerInput}
                                    onChange={(e) => setAnswerInput(e.target.value)}
                                    required
                                />
                                <button type="submit" className="submit-answer-btn">
                                    Submit Answer
                                </button>
                            </form>

                            <hr className="answer-divider" />

                            <div className="answers-section">
                                <h4 className="answers-title">Answers ({questionData.totalAnswers || 0})</h4>
                                {questionData.answers.length === 0 ? (
                                    <p className="no-answers">No answers yet. Be the first to answer!</p>
                                ) : (
                                    questionData.answers.map((answer) => (
                                        <div key={answer.answerId} className="answer-card">
                                            <div className="answer-header">
                                                <div className="author-details">
                                                    <span className="author-name">{answer.user.name || 'Anonymous'}</span>
                                                    <span className="author-username">@{answer.user.username || 'user'}</span>
                                                </div>
                                                <span className="time-ago">{formatTimePost(answer.createdAt)}</span>
                                            </div>
                                            <p className="answer-text">{answer.description}</p>
                                        </div>
                                    ))
                                )}
                            </div>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default Profile;