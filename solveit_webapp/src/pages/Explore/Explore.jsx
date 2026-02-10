import './Explore.css';
import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import Sidebar from "../../components/Sidebar/Sidebar.jsx";
import AxiosConfig from "../../service/AxiosConfig.js";
import { API_ENDPOINTS } from "../../service/ApiEndpoints.js";
import { formatTimePost } from "../../util/UtilMethod.js";
import { toast } from "react-toastify";

const Explore = () => {
    const navigate = useNavigate();
    const [questions, setQuestions] = useState([]);
    const [loading, setLoading] = useState(true);
    const [searchQuery, setSearchQuery] = useState('');
    const [filterType, setFilterType] = useState('feed');
    const [showModal, setShowModal] = useState(false);
    const [selectedQuestion, setSelectedQuestion] = useState(null);
    const [questionData, setQuestionData] = useState(null);
    const [answerInput, setAnswerInput] = useState('');

    useEffect(() => {
        fetchQuestions();
    }, [filterType]);

    const fetchQuestions = async () => {
        try {
            setLoading(true);
            let response;

            switch (filterType) {
                case 'feed':
                    response = await AxiosConfig.get(API_ENDPOINTS.GET_FEEDS);
                    break;
                case 'recent':
                    response = await AxiosConfig.get(API_ENDPOINTS.GET_FEEDS_RECENT);
                    break;
                case 'popular':
                    response = await AxiosConfig.get(API_ENDPOINTS.GET_FEEDS_POPULAR);
                    break;
                default:
                    response = await AxiosConfig.get(API_ENDPOINTS.GET_FEEDS);
            }

            if (response.status === 200) {
                const questionsData = response.data.content;
                setQuestions(questionsData);
            }

            console.log("Questions: ", response.data.content);
        } catch (error) {
            console.error("Error fetching questions:", error);
        } finally {
            setLoading(false);
        }
    };

    const fetchAnswers = async (questionId) => {
        try {
            const response = await AxiosConfig.get(API_ENDPOINTS.GET_QUESTION(questionId));
            if (response.status === 200) {
                setQuestionData(response.data);
                console.log("Question data:", response.data);
            }
        } catch (error) {
            console.error("Error fetching answers:", error);
        }
    };

    const handleLikesQuestion = async (e, questionId) => {
        e.stopPropagation();
        console.log("Liking question ID:", questionId);
        try {
            const response = await AxiosConfig.post(API_ENDPOINTS.LIKE_QUESTION(questionId));
            if (response.status === 201 || response.status === 200) {
                fetchQuestions();
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
                fetchQuestions();
            }
        } catch (error) {
            console.error("Error unliking question:", error);
        }
    };

    const handleSearch = (e) => {
        setSearchQuery(e.target.value);
    };

    const handleQuestionClick = (question) => {
        setSelectedQuestion(question);
        setShowModal(true);
        fetchAnswers(question.id);
    };

    const handleAskQuestion = () => {
        navigate('/questions');
    };

    const handleFilterChange = (newFilter) => {
        setFilterType(newFilter);
    };

    const closeModal = () => {
        setShowModal(false);
        setSelectedQuestion(null);
        setQuestionData(null);
        setAnswerInput('');
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

    const filteredQuestions = questions.filter(q =>
        q.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
        (q.description && q.description.toLowerCase().includes(searchQuery.toLowerCase()))
    );

    return (
        <div className="explore-container">
            <Sidebar />

            <main className="explore-main">
                <div className="explore-header">
                    <div className="header-top">
                        <h1 className="page-title">Explore Questions</h1>
                        <button className="btn-ask-question" onClick={handleAskQuestion}>
                            <i className="bi bi-plus-circle"></i>
                            Ask Question
                        </button>
                    </div>

                    <div className="search-container">
                        <i className="bi bi-search search-icon"></i>
                        <input type="text"
                            className="search-input"
                            placeholder="Search questions..."
                            value={searchQuery}
                            onChange={handleSearch}/>
                    </div>

                    <div className="filter-tabs">
                        <button className={`filter-tab ${filterType === 'feed' ? 'active' : ''}`}
                            onClick={() => handleFilterChange('feed')}>
                            <i className="bi bi-grid"></i>
                            Feed
                        </button>
                        <button className={`filter-tab ${filterType === 'recent' ? 'active' : ''}`}
                            onClick={() => handleFilterChange('recent')}>
                            <i className="bi bi-clock"></i>
                            Recent
                        </button>
                        <button className={`filter-tab ${filterType === 'popular' ? 'active' : ''}`}
                            onClick={() => handleFilterChange('popular')}>
                            <i className="bi bi-fire"></i>
                            Popular
                        </button>
                    </div>
                </div>

                <div className="questions-feed">
                    {loading ? (
                        <div className="loading-state">
                            <div className="spinner"></div>
                            <p>Loading questions...</p>
                        </div>
                    ) : filteredQuestions.length === 0 ? (
                        <div className="empty-state">
                            <i className="bi bi-inbox"></i>
                            <h3>No questions found</h3>
                            <p>Try adjusting your search or be the first to ask a question!</p>
                            <button className="btn-primary" onClick={handleAskQuestion}>
                                Ask Question
                            </button>
                        </div>
                    ) : (
                        filteredQuestions.map((question) => (
                            <div key={question.id}
                                className="question-card"
                                onClick={() => handleQuestionClick(question)}>
                                <div className="question-header">
                                    <div className="author-info">
                                        <div className="author-details">
                                            <span className="author-name">{question.name || 'Anonymous'}</span>
                                            <span className="author-username">{question.username || 'user'}</span>
                                        </div>
                                    </div>
                                    <span className="time-ago">{formatTimePost(question.createdAt)}</span>
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
                                        <span>{question.likes ?? 0}</span>
                                    </div>

                                    <div className={`stat-item dislikes ${question.unliked ? 'active' : ''}`}
                                        onClick={(e) => handleUnLikesQuestion(e, question.id)}>
                                        <i className={`bi ${question.unliked ? 'bi-hand-thumbs-down-fill' : 'bi-hand-thumbs-down'}`}></i>
                                        <span>{question.unlikes ?? 0}</span>
                                    </div>
                                </div>
                            </div>
                        ))
                    )}
                </div>
            </main>

            {/* Question Detail Modal */}
            {showModal && selectedQuestion && questionData && (
                <div className="modal-overlay" onClick={closeModal}>
                    <div className="modal-content-question" onClick={(e) => e.stopPropagation()}>
                        <div className="modal-header">
                            <h3> &nbsp; &nbsp;&nbsp;Question Details</h3>
                            <button className="modal-close" onClick={closeModal}>
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
                                <input type="text"
                                    className="answer-input"
                                    placeholder="Write your answer..."
                                    value={answerInput}
                                    onChange={(e) => setAnswerInput(e.target.value)}
                                    required/>
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

export default Explore;